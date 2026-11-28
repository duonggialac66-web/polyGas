package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.DeliveryReportRequest;
import poly.duantn.dto.DeliveryReportResponse;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.Inventory;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.PaymentStatus;
import poly.duantn.entity.enums.Role;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.DeliveryReportRepository;
import poly.duantn.repository.InventoryRepository;
import poly.duantn.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RESTful Controller quản lý báo cáo giao gas và công nợ shipper trong ngày
 * Chuẩn hóa URL RESTful resource: /api/v1/delivery-reports (hỗ trợ alias /api/v1/employment)
 */
@RestController
@RequestMapping({"/api/v1/delivery-reports", "/api/v1/employment"})
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DeliveryReportController {

    private final DeliveryReportRepository deliveryReportRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    /**
     * Lấy danh sách báo cáo giao gas trong ngày của nhân viên
     * GET /api/v1/delivery-reports/today
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<DeliveryReportResponse>>> getTodayReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        log.info("Lấy danh sách báo cáo giao gas ngày: {}", queryDate);

        List<DeliveryReport> reports = deliveryReportRepository.findAllByReportDateWithDetails(queryDate);

        List<DeliveryReportResponse> responses = reports.stream()
                .map(DeliveryReportResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responses, "Lấy danh sách giao gas thành công"));
    }

    /**
     * Lấy tất cả báo cáo giao hàng (có thể lọc theo trạng thái PAID/DEBT)
     * GET /api/v1/delivery-reports
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryReportResponse>>> getAllReports(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String keyword
    ) {
        List<DeliveryReport> reports = deliveryReportRepository.findAllWithDetails();

        if (status != null) {
            reports = reports.stream()
                    .filter(r -> r.getPaymentStatus() == status)
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            reports = reports.stream()
                    .filter(r -> (r.getCustomerName() != null && r.getCustomerName().toLowerCase().contains(kw))
                            || (r.getNotes() != null && r.getNotes().toLowerCase().contains(kw)))
                    .collect(Collectors.toList());
        }

        List<DeliveryReportResponse> responses = reports.stream()
                .map(DeliveryReportResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responses, "Thành công"));
    }

    /**
     * Nhân viên tạo phiếu báo cáo giao gas mới
     * POST /api/v1/delivery-reports
     */
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> createReport(
            @Valid @RequestBody DeliveryReportRequest request
    ) {
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập tên khách hàng");
        }

        int quantity = (request.getQuantity() != null && request.getQuantity() > 0) ? request.getQuantity() : 1;
        double unitPrice = (request.getUnitPrice() != null) ? request.getUnitPrice() : 0.0;
        double total = (request.getTotal() != null && request.getTotal() > 0)
                ? request.getTotal()
                : (quantity * unitPrice);

        // Tìm loại bình tương ứng trong kho
        String containerType = (request.getContainerType() != null && !request.getContainerType().trim().isEmpty())
                ? request.getContainerType().trim()
                : "Bình 12kg Xám";

        Inventory inventory = inventoryRepository.findByContainerType(containerType)
                .orElseGet(() -> {
                    List<Inventory> all = inventoryRepository.findAll();
                    if (!all.isEmpty()) return all.get(0);
                    return inventoryRepository.save(Inventory.builder()
                            .containerType(containerType)
                            .fullQuantity(100)
                            .emptyQuantity(50)
                            .warningThreshold(10)
                            .build());
                });

        // Kiểm tra tồn kho bình đầy
        if (inventory.getFullQuantity() < quantity) {
            throw new BadRequestException("Số lượng bình đầy trong kho (" + inventory.getFullQuantity() + ") không đủ để giao!");
        }

        // Tự động trừ tồn kho bình đầy khi giao thành công
        inventory.setFullQuantity(inventory.getFullQuantity() - quantity);
        inventoryRepository.save(inventory);

        // Tìm nhân viên lập đơn (ưu tiên employeeId truyền lên, hoặc tài khoản shipper01 mẫu)
        User employee = null;
        if (request.getEmployeeId() != null) {
            employee = userRepository.findById(request.getEmployeeId()).orElse(null);
        }
        if (employee == null) {
            employee = userRepository.findByUsername("shipper01")
                    .orElseGet(() -> userRepository.findByRole(Role.EMPLOYEE).stream().findFirst()
                            .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null)));
        }

        DeliveryReport report = DeliveryReport.builder()
                .customerName(request.getCustomerName().trim())
                .inventory(inventory)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .total(total)
                .actualReceived(request.getPaymentStatus() == PaymentStatus.PAID ? total : 0.0)
                .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PAID)
                .notes(request.getNotes())
                .reportDate(request.getReportDate() != null ? request.getReportDate() : LocalDate.now())
                .employee(employee)
                .build();

        DeliveryReport saved = deliveryReportRepository.save(report);
        log.info("Nhân viên {} đã lập báo cáo giao gas cho khách: {}, SL: {}",
                employee != null ? employee.getName() : "Unknown", saved.getCustomerName(), saved.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(DeliveryReportResponse.fromEntity(saved), "Tạo phiếu giao gas thành công"));
    }

    /**
     * Cập nhật thông tin phiếu giao gas
     * PUT /api/v1/delivery-reports/{id}
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> updateReport(
            @PathVariable UUID id,
            @Valid @RequestBody DeliveryReportRequest request
    ) {
        DeliveryReport report = deliveryReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu giao gas", "id", id));

        if (request.getCustomerName() != null) report.setCustomerName(request.getCustomerName().trim());
        if (request.getQuantity() != null) report.setQuantity(request.getQuantity());
        if (request.getUnitPrice() != null) report.setUnitPrice(request.getUnitPrice());
        if (request.getNotes() != null) report.setNotes(request.getNotes());
        if (request.getPaymentStatus() != null) report.setPaymentStatus(request.getPaymentStatus());

        // Tự động tính lại tổng tiền
        int qty = report.getQuantity() != null ? report.getQuantity() : 1;
        double price = report.getUnitPrice() != null ? report.getUnitPrice() : 0.0;
        report.setTotal(qty * price);

        if (report.getPaymentStatus() == PaymentStatus.PAID) {
            report.setActualReceived(report.getTotal());
        } else {
            report.setActualReceived(0.0);
        }

        // Đổi loại bình nếu có
        if (request.getContainerType() != null) {
            inventoryRepository.findByContainerType(request.getContainerType())
                    .ifPresent(report::setInventory);
        }

        DeliveryReport updated = deliveryReportRepository.save(report);
        return ResponseEntity.ok(ApiResponse.ok(DeliveryReportResponse.fromEntity(updated), "Cập nhật thành công"));
    }

    /**
     * Đổi nhanh trạng thái: Đã thanh toán (PAID) <-> Ghi nợ (DEBT)
     * PATCH /api/v1/delivery-reports/{id}/toggle-payment
     */
    @PatchMapping("/{id}/toggle-payment")
    @Transactional
    public ResponseEntity<ApiResponse<DeliveryReportResponse>> togglePayment(@PathVariable UUID id) {
        DeliveryReport report = deliveryReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu giao gas", "id", id));

        PaymentStatus nextStatus = (report.getPaymentStatus() == PaymentStatus.PAID)
                ? PaymentStatus.DEBT
                : PaymentStatus.PAID;
        report.setPaymentStatus(nextStatus);
        report.setActualReceived(nextStatus == PaymentStatus.PAID ? report.getTotal() : 0.0);

        DeliveryReport updated = deliveryReportRepository.save(report);
        return ResponseEntity.ok(ApiResponse.ok(
                DeliveryReportResponse.fromEntity(updated),
                "Đã chuyển trạng thái: " + (nextStatus == PaymentStatus.PAID ? "Đã thanh toán" : "Ghi nợ")
        ));
    }

    /**
     * Xóa phiếu báo cáo giao gas
     * DELETE /api/v1/delivery-reports/{id}
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable UUID id) {
        DeliveryReport report = deliveryReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu giao gas", "id", id));

        deliveryReportRepository.delete(report);
        return ResponseEntity.ok(ApiResponse.<Void>ok(null, "Xóa phiếu giao gas thành công"));
    }
}
package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.DebtResponse;
import poly.duantn.dto.PayDebtRequest;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.enums.PaymentStatus;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.DeliveryReportRepository;
import poly.duantn.service.DebtService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cài đặt nghiệp vụ Sổ nợ
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DebtServiceImpl implements DebtService {

    private final DeliveryReportRepository deliveryReportRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DebtResponse> getDebts(String customer, UUID employeeId) {
        log.info("Lấy danh sách sổ nợ với filter: customer='{}', employeeId={}", customer, employeeId);

        // Lấy toàn bộ đơn đang có trạng thái ghi nợ
        List<DeliveryReport> debtReports = deliveryReportRepository.findByPaymentStatus(PaymentStatus.DEBT);

        return debtReports.stream()
                // Lọc theo shipper nếu có truyền employeeId
                .filter(report -> {
                    if (employeeId == null) return true;
                    return report.getEmployee() != null && employeeId.equals(report.getEmployee().getId());
                })
                // Lọc theo từ khóa khách hàng (tên hoặc số điện thoại)
                .filter(report -> {
                    if (customer == null || customer.trim().isEmpty()) return true;
                    String kw = customer.trim().toLowerCase();

                    boolean matchName = report.getCustomerName() != null 
                            && report.getCustomerName().toLowerCase().contains(kw);

                    boolean matchPhone = report.getCustomer() != null 
                            && report.getCustomer().getPhone() != null 
                            && report.getCustomer().getPhone().contains(kw);

                    return matchName || matchPhone;
                })
                // Sắp xếp ngày báo cáo mới nhất lên trước
                .sorted((r1, r2) -> {
                    if (r1.getReportDate() != null && r2.getReportDate() != null) {
                        int dateCmp = r2.getReportDate().compareTo(r1.getReportDate());
                        if (dateCmp != 0) return dateCmp;
                    }
                    if (r1.getCreatedAt() != null && r2.getCreatedAt() != null) {
                        return r2.getCreatedAt().compareTo(r1.getCreatedAt());
                    }
                    return 0;
                })
                .map(DebtResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DebtResponse payDebt(UUID reportId, PayDebtRequest request) {
        if (request == null || request.getAmountPaid() == null || request.getAmountPaid() <= 0) {
            throw new BadRequestException("Số tiền thanh toán phải lớn hơn 0");
        }

        DeliveryReport report = deliveryReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu giao gas", "reportId", reportId));

        double total = (report.getTotal() != null) ? report.getTotal() : 0.0;
        double currentActual = (report.getActualReceived() != null) ? report.getActualReceived() : 0.0;
        double remainingBefore = Math.max(0.0, total - currentActual);

        if (remainingBefore <= 0 && report.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Đơn hàng này đã được thanh toán đủ, không còn nợ.");
        }

        double amountPaid = request.getAmountPaid();
        double newActual = currentActual + amountPaid;
        report.setActualReceived(newActual);

        // Logic cốt lõi: Nếu actualReceived >= total thì tự động đổi payment_status sang PaymentStatus.PAID
        if (newActual >= total) {
            report.setPaymentStatus(PaymentStatus.PAID);
            log.info("Đơn {} đã hoàn tất thanh toán (Đã thu {} / Tổng {}). Trạng thái chuyển sang PAID.",
                    reportId, newActual, total);
        } else {
            log.info("Thu nợ đơn {}: Thu thêm {}, đã thu lũy kế {} / Tổng {}, còn nợ {}",
                    reportId, amountPaid, newActual, total, (total - newActual));
        }

        // Bổ sung ghi chú nếu có
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            String existingNotes = (report.getNotes() != null && !report.getNotes().trim().isEmpty()) 
                    ? report.getNotes() + " | " 
                    : "";
            report.setNotes(existingNotes + "Thu nợ: " + String.format("%,.0f đ", amountPaid) + " (" + request.getNotes().trim() + ")");
        }

        DeliveryReport saved = deliveryReportRepository.save(report);
        return DebtResponse.fromEntity(saved);
    }
}

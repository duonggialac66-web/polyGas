package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.DailyReconciliationResponse;
import poly.duantn.dto.DeliveryReportResponse;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.User;
import poly.duantn.repository.DeliveryReportRepository;
import poly.duantn.repository.UserRepository;
import poly.duantn.service.DeliveryReportService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cài đặt nghiệp vụ Lịch sử báo cáo giao hàng và Đối soát cuối ngày
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryReportServiceImpl implements DeliveryReportService {

    private final DeliveryReportRepository deliveryReportRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryReportResponse> getDeliveryHistory(LocalDate fromDate, LocalDate toDate, UUID employeeId) {
        // Chuẩn hóa khoảng ngày: mặc định 30 ngày gần nhất nếu để trống
        LocalDate end = (toDate != null) ? toDate : LocalDate.now();
        LocalDate start = (fromDate != null) ? fromDate : end.minusDays(30);

        if (start.isAfter(end)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        log.info("Truy vấn lịch sử giao hàng từ {} đến {}, employeeId: {}", start, end, employeeId);

        List<DeliveryReport> reports;
        if (employeeId != null) {
            reports = deliveryReportRepository.findByEmployeeIdAndReportDateBetween(employeeId, start, end);
        } else {
            reports = deliveryReportRepository.findByReportDateBetween(start, end);
        }

        return reports.stream()
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
                .map(DeliveryReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DailyReconciliationResponse getDailyReconciliation(LocalDate reportDate, UUID employeeId) {
        LocalDate queryDate = (reportDate != null) ? reportDate : LocalDate.now();
        log.info("Tính toán đối soát tiền mặt cuối ngày: {}, employeeId: {}", queryDate, employeeId);

        List<DeliveryReport> reports;
        String employeeName = "Toàn hệ thống (Tất cả shipper)";

        if (employeeId != null) {
            reports = deliveryReportRepository.findByEmployeeIdAndReportDate(employeeId, queryDate);
            employeeName = userRepository.findById(employeeId)
                    .map(User::getName)
                    .orElse("Shipper ID " + employeeId);
        } else {
            reports = deliveryReportRepository.findAll().stream()
                    .filter(r -> queryDate.equals(r.getReportDate()))
                    .collect(Collectors.toList());
        }

        // Logic cốt lõi: Sử dụng Java Stream tính tổng các chỉ số đối soát
        long totalOrders = reports.size();

        int totalCylinders = reports.stream()
                .mapToInt(r -> (r.getQuantity() != null) ? r.getQuantity() : 0)
                .sum();

        // Tổng tiền mặt shipper phải bàn giao lại cho thủ quỹ = SUM(actualReceived)
        double totalCashToHandover = reports.stream()
                .mapToDouble(r -> (r.getActualReceived() != null) ? r.getActualReceived() : 0.0)
                .sum();

        // Tổng doanh số đơn hàng = SUM(total)
        double totalSalesAmount = reports.stream()
                .mapToDouble(r -> (r.getTotal() != null) ? r.getTotal() : 0.0)
                .sum();

        // Tổng tiền bị khách nợ lại = SUM(total - actualReceived)
        double totalRemainingDebt = reports.stream()
                .mapToDouble(r -> {
                    double tot = (r.getTotal() != null) ? r.getTotal() : 0.0;
                    double act = (r.getActualReceived() != null) ? r.getActualReceived() : 0.0;
                    return Math.max(0.0, tot - act);
                })
                .sum();

        return DailyReconciliationResponse.builder()
                .reportDate(queryDate)
                .employeeId(employeeId)
                .employeeName(employeeName)
                .totalOrders(totalOrders)
                .totalCylinders(totalCylinders)
                .totalCashToHandover(totalCashToHandover)
                .totalRemainingDebt(totalRemainingDebt)
                .totalSalesAmount(totalSalesAmount)
                .build();
    }
}

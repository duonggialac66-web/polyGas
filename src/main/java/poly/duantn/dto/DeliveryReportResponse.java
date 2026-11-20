package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO dữ liệu phiếu giao gas gửi về Frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReportResponse {

    private UUID id;
    private String customerName;
    private String containerType;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
    private PaymentStatus paymentStatus;
    private String notes;
    private LocalDate reportDate;
    private UUID employeeId;
    private String employeeName;
    private LocalDateTime createdAt;

    public static DeliveryReportResponse fromEntity(DeliveryReport report) {
        if (report == null) return null;
        return DeliveryReportResponse.builder()
                .id(report.getId())
                .customerName(report.getCustomerName())
                .containerType(report.getInventory() != null ? report.getInventory().getContainerType() : "Không xác định")
                .quantity(report.getQuantity())
                .unitPrice(report.getUnitPrice())
                .total(report.getTotal())
                .paymentStatus(report.getPaymentStatus())
                .notes(report.getNotes())
                .reportDate(report.getReportDate())
                .employeeId(report.getEmployee() != null ? report.getEmployee().getId() : null)
                .employeeName(report.getEmployee() != null ? report.getEmployee().getName() : "Nhân viên")
                .createdAt(report.getCreatedAt())
                .build();
    }
}

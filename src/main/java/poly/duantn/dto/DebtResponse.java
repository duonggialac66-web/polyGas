package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.DeliveryReport;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO phản hồi thông tin sổ nợ của đơn giao gas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtResponse {

    private UUID reportId;
    private String customerName;
    private String customerPhone;
    private String employeeName;
    private Double total;
    private Double actualReceived;
    private Double remainingDebt;
    private LocalDate reportDate;

    // Các trường thông tin bổ sung
    private String containerType;
    private Integer quantity;
    private String notes;

    public static DebtResponse fromEntity(DeliveryReport report) {
        if (report == null) return null;

        double total = (report.getTotal() != null) ? report.getTotal() : 0.0;
        double actualReceived = (report.getActualReceived() != null) ? report.getActualReceived() : 0.0;
        double remainingDebt = Math.max(0.0, total - actualReceived);

        String phone = null;
        if (report.getCustomer() != null && report.getCustomer().getPhone() != null) {
            phone = report.getCustomer().getPhone();
        }

        String employeeName = "Nhân viên";
        if (report.getEmployee() != null && report.getEmployee().getName() != null) {
            employeeName = report.getEmployee().getName();
        }

        String containerType = "Bình gas";
        if (report.getInventory() != null && report.getInventory().getContainerType() != null) {
            containerType = report.getInventory().getContainerType();
        }

        return DebtResponse.builder()
                .reportId(report.getId())
                .customerName(report.getCustomerName())
                .customerPhone(phone)
                .employeeName(employeeName)
                .total(total)
                .actualReceived(actualReceived)
                .remainingDebt(remainingDebt)
                .reportDate(report.getReportDate())
                .containerType(containerType)
                .quantity(report.getQuantity())
                .notes(report.getNotes())
                .build();
    }
}

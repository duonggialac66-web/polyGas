package poly.duantn.dto;

import lombok.*;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO dữ liệu trả về thông tin phiếu báo cáo giao gas hoàn chỉnh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReportResponse {

    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private UUID customerId;
    private String customerName;
    private UUID inventoryId;
    private String containerType;
    private Integer quantity;
    private Double unitPrice;
    private Double total;
    private Double actualReceived;
    private Integer returnedShells;
    private Integer debtShells;
    private String shellStatus;
    private PaymentStatus paymentStatus;
    private String notes;
    private String receiptUrl;
    private LocalDate reportDate;
    private LocalDateTime createdAt;

    /**
     * Chuyển đổi từ DeliveryReport entity sang DeliveryReportResponse DTO
     * Có kiểm tra an toàn cho các liên kết Lazy Hibernate Proxy
     */
    public static DeliveryReportResponse fromEntity(DeliveryReport report) {
        if (report == null) {
            return null;
        }

        int qty = (report.getQuantity() != null) ? report.getQuantity() : 0;
        double total = (report.getTotal() != null) ? report.getTotal() : 0.0;
        double actual = (report.getActualReceived() != null) ? report.getActualReceived() : 0.0;

        // Trích xuất an toàn employeeName để tránh LazyInitializationException
        UUID employeeId = null;
        String employeeName = "Nhân viên giao gas";
        try {
            if (report.getEmployee() != null) {
                employeeId = report.getEmployee().getId();
                if (report.getEmployee().getName() != null) {
                    employeeName = report.getEmployee().getName();
                }
            }
        } catch (Exception e) {
            // Trường hợp Hibernate Session bị đóng
            if (report.getEmployee() != null) {
                employeeId = report.getEmployee().getId();
            }
        }

        // Trích xuất an toàn customerId
        UUID customerId = null;
        try {
            if (report.getCustomer() != null) {
                customerId = report.getCustomer().getId();
            }
        } catch (Exception ignored) {
        }

        // Trích xuất an toàn inventory
        UUID inventoryId = null;
        String containerType = "Bình gas tiêu chuẩn";
        try {
            if (report.getInventory() != null) {
                inventoryId = report.getInventory().getId();
                if (report.getInventory().getContainerType() != null) {
                    containerType = report.getInventory().getContainerType();
                }
            }
        } catch (Exception ignored) {
        }

        return DeliveryReportResponse.builder()
                .id(report.getId())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .customerId(customerId)
                .customerName(report.getCustomerName())
                .inventoryId(inventoryId)
                .containerType(containerType)
                .quantity(qty)
                .unitPrice(report.getUnitPrice())
                .total(total)
                .actualReceived(actual)
                .returnedShells(qty)
                .debtShells(0)
                .shellStatus("RETURNED")
                .paymentStatus(report.getPaymentStatus())
                .notes(report.getNotes())
                .receiptUrl(report.getReceiptUrl())
                .reportDate(report.getReportDate())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
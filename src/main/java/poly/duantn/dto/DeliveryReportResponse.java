package poly.duantn.dto;

import lombok.*;
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
}

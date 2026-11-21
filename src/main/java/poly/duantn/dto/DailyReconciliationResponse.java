package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO phản hồi số liệu đối soát tiền mặt cuối ngày của shipper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReconciliationResponse {

    private LocalDate reportDate;
    private UUID employeeId;
    private String employeeName;
    private Long totalOrders;
    private Integer totalCylinders;
    private Double totalCashToHandover;
    private Double totalRemainingDebt;
    private Double totalSalesAmount;
}

package poly.duantn.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request payload cho nhân viên/shipper ghi nhận chi phí vận hành trong ngày
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRequest {

    @NotNull(message = "Mã nhân viên (employeeId) không được để trống")
    private UUID employeeId;

    private LocalDate expenseDate;

    @NotBlank(message = "Mô tả chi phí (xăng xe, ăn trưa...) không được để trống")
    private String description;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Số tiền phải lớn hơn 0")
    private Double amount;

    private String notes;
}

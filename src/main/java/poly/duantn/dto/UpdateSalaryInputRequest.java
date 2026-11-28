package poly.duantn.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Cập nhật biến số chấm công cho nhân viên theo tháng (Phân hệ Lương - Hà Trọng Phúc)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSalaryInputRequest {

    /**
     * Số ngày công thực tế trong tháng (mặc định 26.0)
     */
    @NotNull(message = "Số ngày công không được để trống")
    @DecimalMin(value = "0.0", message = "Số ngày công không được âm")
    @DecimalMax(value = "31.0", message = "Số ngày công tối đa là 31 ngày")
    @Builder.Default
    private Double workDays = 26.0;

    /**
     * Hệ số tăng ca (mặc định 1.0)
     */
    @NotNull(message = "Hệ số tăng ca không được để trống")
    @DecimalMin(value = "1.0", message = "Hệ số tăng ca tối thiểu là 1.0")
    @DecimalMax(value = "5.0", message = "Hệ số tăng ca tối đa là 5.0")
    @Builder.Default
    private Double overtime = 1.0;

    /**
     * Tiền thưởng thêm / phụ cấp phát sinh
     */
    @NotNull(message = "Tiền thưởng không được để trống")
    @PositiveOrZero(message = "Tiền thưởng không được âm")
    @Builder.Default
    private Double bonus = 0.0;
}

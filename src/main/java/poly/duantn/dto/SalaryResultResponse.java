package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO Bảng kết quả chi tiết lương tháng đã tính toán theo SpEL Engine (Hà Trọng Phúc)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryResultResponse {

    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private String username;
    private String phone;
    private Role role;
    private String month;

    private Double baseSalary;
    private Double workDays;
    private Double overtime;
    private Double bonus;

    /**
     * Tổng số bình gas đã giao thành công trong tháng
     */
    private Integer deliveredCylinders;

    /**
     * Tổng tiền hoa hồng theo sản lượng bình gas (#commissionTotal)
     */
    private Double commissionTotal;

    /**
     * Tổng lương thực nhận sau khi chạy biểu thức SpEL (calculatedSalary)
     */
    private Double calculatedSalary;

    private String formulaName;
    private String formulaExpression;
    private String status;
    private LocalDateTime updatedAt;
}

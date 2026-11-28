package poly.duantn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.enums.Role;

import java.time.LocalDate;

/**
 * DTO Yêu cầu tạo mới nhân viên (Phân hệ Quản lý Nhân sự - Hà Trọng Phúc)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequest {

    @NotBlank(message = "Họ và tên nhân viên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Tên đăng nhập chỉ được chứa chữ cái, số, dấu chấm, gạch dưới hoặc gạch ngang")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải có tối thiểu 6 ký tự")
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Số điện thoại không hợp lệ (phải gồm 10 số bắt đầu bằng 03, 05, 07, 08, 09)")
    private String phone;

    @NotNull(message = "Lương cơ bản không được để trống")
    @PositiveOrZero(message = "Lương cơ bản phải lớn hơn hoặc bằng 0")
    @Builder.Default
    private Double baseSalary = 0.0;

    @NotNull(message = "Ngày bắt đầu làm việc không được để trống")
    private LocalDate startDate;

    @Builder.Default
    private Role role = Role.EMPLOYEE;
}

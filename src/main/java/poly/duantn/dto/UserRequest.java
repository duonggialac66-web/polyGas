package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu thêm mới / cập nhật nhân viên từ Frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private String name;
    private String username;
    private String password;
    private Role role;
    private String phone;
    private LocalDate startDate;
    private UserStatus status;
    private Double baseSalary;
    private String avatarUrl;
}


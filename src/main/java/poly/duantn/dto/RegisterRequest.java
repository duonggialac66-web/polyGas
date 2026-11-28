package poly.duantn.dto;

import lombok.Data;
import poly.duantn.entity.enums.Role;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String phone;
    private Role role;
}
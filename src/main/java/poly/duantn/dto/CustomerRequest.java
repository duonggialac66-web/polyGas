package poly.duantn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload cho tạo mới và cập nhật thông tin khách hàng (GPS)
 * Sử dụng Jakarta Bean Validation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 2, max = 255, message = "Tên khách hàng phải từ 2 đến 255 ký tự")
    private String name;

    @Pattern(regexp = "^(0[35789][0-9]{8})?$", message = "Số điện thoại phải gồm 10 chữ số hợp lệ (VD: 0908123456)")
    private String phone;

    private String address;

    private Double latitude;

    private Double longitude;

    private String notes;
}
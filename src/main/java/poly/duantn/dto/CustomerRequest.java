package poly.duantn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload cho tạo mới và cập nhật thông tin khách hàng (GPS)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String name;

    private String phone;

    private String address;

    private Double latitude;

    private Double longitude;

    private String notes;
}

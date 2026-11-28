package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload trả về thông tin khách hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private UUID id;
    private String name;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private String notes;
    private String nameHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
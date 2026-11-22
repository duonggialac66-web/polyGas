package poly.duantn.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO dữ liệu trả về thông tin tồn kho kèm cờ cảnh báo hết hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private UUID id;
    private String containerType;
    private Integer fullQuantity;
    private Integer emptyQuantity;
    private Integer warningThreshold;
    private Boolean isWarning;
    private LocalDateTime updatedAt;
}

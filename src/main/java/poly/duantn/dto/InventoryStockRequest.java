package poly.duantn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO yêu cầu điều chỉnh nhập / xuất số lượng tồn kho gas
 * Cài đặt Bean Validation constraints để ngăn chặn số lượng âm hoặc thao tác rỗng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStockRequest {

    @NotNull(message = "Số lượng điều chỉnh kho không được để trống")
    @Min(value = 1, message = "Số lượng điều chỉnh phải lớn hơn 0")
    private Integer quantity;

    @NotBlank(message = "Loại thao tác kho không được để trống (Ví dụ: IMPORT_FULL, EXPORT_FULL, IMPORT_EMPTY, EXPORT_EMPTY)")
    private String type;
}
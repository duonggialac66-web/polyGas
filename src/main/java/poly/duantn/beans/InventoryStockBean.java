package poly.duantn.beans;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * JavaBean phục vụ nhận và bắt lỗi dữ liệu từ Form Nhập / Xuất Kho Gas (Trang 2)
 * Tuân thủ quy chuẩn JavaBeans (Default constructor, Getters/Setters, Serializable)
 * và tích hợp Jakarta Bean Validation Constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStockBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID loại bình gas trong kho cần điều chỉnh
     */
    @NotNull(message = "Vui lòng chọn loại bình gas cần điều chỉnh")
    private UUID inventoryId;

    /**
     * Số lượng bình đầy hoặc vỏ rỗng cần nhập/xuất
     */
    @NotNull(message = "Số lượng điều chỉnh kho không được để trống")
    @Min(value = 1, message = "Số lượng điều chỉnh kho phải từ 1 trở lên")
    @Max(value = 10000, message = "Số lượng trong một lần thao tác không được vượt quá 10,000")
    private Integer quantity;

    /**
     * Loại thao tác kho:
     * - IMPORT_FULL: Nhập bình đầy từ nhà máy
     * - EXPORT_FULL: Xuất bình đầy đi giao
     * - IMPORT_EMPTY: Thu hồi vỏ rỗng vào kho
     * - EXPORT_EMPTY: Trả vỏ rỗng về nhà máy chiết nạp
     */
    @NotBlank(message = "Loại thao tác kho không được để trống")
    @Pattern(
        regexp = "^(IMPORT_FULL|EXPORT_FULL|IMPORT_EMPTY|EXPORT_EMPTY)$",
        message = "Loại thao tác không hợp lệ! Chỉ chấp nhận: IMPORT_FULL, EXPORT_FULL, IMPORT_EMPTY hoặc EXPORT_EMPTY"
    )
    private String type;

    /**
     * Ghi chú lý do nhập xuất kho (nếu có)
     */
    @Size(max = 500, message = "Ghi chú điều chỉnh kho không được vượt quá 500 ký tự")
    private String notes;
}

package poly.duantn.beans;

import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * JavaBean phục vụ nhận và bắt lỗi dữ liệu từ Form Báo Cáo Giao Gas (Trang 1)
 * Tuân thủ quy chuẩn JavaBeans (Default constructor, Getters/Setters, Serializable)
 * và tích hợp Jakarta Bean Validation Constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReportBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID nhân viên lập phiếu
     */
    private UUID employeeId;

    /**
     * ID khách hàng nếu đã có trong hệ thống danh bạ (cho phép null nếu là khách vãng lai)
     */
    private UUID customerId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 2, max = 150, message = "Tên khách hàng phải từ 2 đến 150 ký tự")
    private String customerName;

    @NotNull(message = "Vui lòng chọn loại bình gas trong kho")
    private UUID inventoryId;

    @NotBlank(message = "Tên chủng loại bình gas không được để trống")
    private String containerType;

    @NotNull(message = "Số lượng bình giao không được để trống")
    @Min(value = 1, message = "Số lượng bình gas giao phải từ 1 bình trở lên")
    @Max(value = 500, message = "Số lượng bình giao trong 1 chuyến không được vượt quá 500 bình")
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @PositiveOrZero(message = "Đơn giá bình gas không được là số âm")
    private Double unitPrice;

    /**
     * Thành tiền đơn hàng
     */
    private Double total;

    @NotNull(message = "Số tiền thực thu từ khách không được để trống")
    @PositiveOrZero(message = "Số tiền thực nhận không được là số âm")
    @Builder.Default
    private Double actualReceived = 0.0;

    /**
     * Số lượng vỏ bình rỗng khách hoàn trả (Nếu bỏ trống = mặc định trả đủ vỏ)
     */
    @PositiveOrZero(message = "Số lượng vỏ bình trả không được là số âm")
    private Integer returnedShells;

    /**
     * Số lượng vỏ bình khách còn nợ (ghi nhận để shipper thu hồi hoàn trả về sau)
     */
    private Integer debtShells;

    /**
     * Trạng thái vỏ: RETURNED (Đã trả đủ) hoặc OWING (Còn nợ vỏ)
     */
    private String shellStatus;

    @Size(max = 1000, message = "Ghi chú giao hàng không được vượt quá 1000 ký tự")
    private String notes;

    @Size(max = 500, message = "Đường dẫn ảnh chứng từ/biên lai không được vượt quá 500 ký tự")
    private String receiptUrl;

    /**
     * Tự động xác định số lượng vỏ trả và nợ vỏ
     * Quy tắc: Nếu returnedShells == null (bỏ trống) -> Trả đủ 100% vỏ giao
     */
    public void resolveShellReturn() {
        int qty = this.quantity != null ? this.quantity : 1;
        if (this.returnedShells == null) {
            this.returnedShells = qty;
        }
        this.debtShells = Math.max(0, qty - this.returnedShells);
        this.shellStatus = this.debtShells == 0 ? "RETURNED" : "OWING";
    }

    /**
     * Tự động tính thành tiền nếu chưa có
     */
    public Double calculateTotal() {
        if (this.quantity != null && this.unitPrice != null) {
            this.total = this.quantity * this.unitPrice;
        } else {
            this.total = 0.0;
        }
        return this.total;
    }

    /**
     * Tự động kiểm tra trạng thái thanh toán
     * @return true nếu thu đủ (PAID), false nếu còn nợ (DEBT)
     */
    public boolean isPaidInFull() {
        Double targetTotal = this.total != null ? this.total : calculateTotal();
        return this.actualReceived != null && this.actualReceived >= targetTotal;
    }
}

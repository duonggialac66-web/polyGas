package poly.duantn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

/**
 * DTO nhận dữ liệu tạo mới báo cáo giao gas
 * Đã cài đặt đầy đủ các Bean Validation constraints để kiểm soát dữ liệu đầu vào.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryReportRequest {

    /**
     * ID nhân viên lập phiếu (có thể null nếu lấy từ context xác thực của Token)
     */
    private UUID employeeId;

    /**
     * ID khách hàng trong danh bạ (cho phép null nếu là khách vãng lai)
     */
    private UUID customerId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @NotNull(message = "Vui lòng chọn loại bình gas trong kho")
    private UUID inventoryId;

    @NotNull(message = "Số lượng bình giao không được để trống")
    @Min(value = 1, message = "Số lượng bình giao phải từ 1 trở lên")
    private Integer quantity;

    @NotNull(message = "Đơn giá không được để trống")
    @PositiveOrZero(message = "Đơn giá không được là số âm")
    private Double unitPrice;

    /**
     * Tổng tiền đơn hàng (nếu để trống, service tự tính bằng quantity * unitPrice)
     */
    private Double total;

    @PositiveOrZero(message = "Số tiền thực nhận từ khách không được là số âm")
    @Builder.Default
    private Double actualReceived = 0.0;

    /**
     * Số lượng vỏ bình khách hoàn trả (nếu để trống hoặc null -> mặc định bằng quantity: trả đủ vỏ)
     */
    @PositiveOrZero(message = "Số lượng vỏ bình trả không được là số âm")
    private Integer returnedShells;

    /**
     * Số lượng vỏ bình khách còn nợ (được hệ thống ghi nhận để hoàn trả sau)
     */
    private Integer debtShells;

    private String notes;

    private String receiptUrl;
}

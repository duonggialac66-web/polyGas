package poly.duantn.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO nhận dữ liệu tạo/sửa phiếu báo cáo giao gas của nhân viên
 * Sử dụng Jakarta Bean Validation bắt lỗi tự động
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReportRequest {

    /**
     * ID khách hàng trong danh bạ (cho phép null nếu là khách vãng lai)
     */
    private UUID customerId;

    /**
     * Tên khách hàng
     */
    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 2, max = 255, message = "Tên khách hàng phải từ 2 đến 255 ký tự")
    private String customerName;

    /**
     * ID loại bình gas trong kho (nếu có)
     */
    private UUID inventoryId;

    /**
     * Loại bình gas (VD: "Bình 12kg Xám", "Bình 12kg Đỏ", "Bình 45kg Công Nghiệp", "Gas Lon Mini")
     */
    @NotBlank(message = "Vui lòng chọn loại bình gas")
    private String containerType;

    /**
     * Số lượng bình giao
     */
    @NotNull(message = "Số lượng bình không được để trống")
    @Min(value = 1, message = "Số lượng bình phải từ 1 trở lên")
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Đơn giá mỗi bình
     */
    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 0, message = "Đơn giá không được là số âm")
    @Builder.Default
    private Double unitPrice = 0.0;

    /**
     * Thành tiền (quantity * unitPrice)
     */
    private Double total;

    /**
     * Số tiền thực thu từ khách
     */
    @Builder.Default
    private Double actualReceived = 0.0;

    /**
     * Số lượng vỏ bình khách đã trả lại
     */
    @Builder.Default
    private Integer returnedShells = 0;

    /**
     * Ghi chú giao hàng
     */
    private String notes;

    /**
     * Trạng thái thanh toán (PAID: Đã thanh toán, DEBT: Ghi nợ)
     */
    @NotNull(message = "Trạng thái thanh toán không được để trống")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    /**
     * Ngày báo cáo (mặc định hôm nay nếu null)
     */
    private LocalDate reportDate;

    /**
     * ID nhân viên lập đơn (nếu có)
     */
    private UUID employeeId;
}
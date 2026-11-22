package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO nhận dữ liệu tạo/sửa phiếu báo cáo giao gas của nhân viên
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReportRequest {

    /**
     * Tên khách hàng
     */
    private String customerName;

    /**
     * Loại bình gas (VD: "Bình 12kg Xám", "Bình 12kg Đỏ", "Bình 45kg Công Nghiệp", "Gas Lon Mini")
     */
    private String containerType;

    /**
     * Số lượng bình giao
     */
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Đơn giá mỗi bình
     */
    @Builder.Default
    private Double unitPrice = 0.0;

    /**
     * Thành tiền (quantity * unitPrice)
     */
    private Double total;

    /**
     * Ghi chú giao hàng
     */
    private String notes;

    /**
     * Trạng thái thanh toán (PAID: Đã thanh toán, DEBT: Ghi nợ)
     */
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


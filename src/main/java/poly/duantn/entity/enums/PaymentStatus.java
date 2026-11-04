package poly.duantn.entity.enums;

/**
 * Trạng thái thanh toán của báo cáo giao gas / đơn hàng
 */
public enum PaymentStatus {

    /**
     * Đã thanh toán (Paid):
     * - Khách hàng đã thanh toán đầy đủ 100% tiền hàng ngay khi nhận gas
     *   (Số tiền thực thu >= Tổng giá trị đơn hàng).
     */
    PAID,

    /**
     * Ghi nợ / Còn nợ (Debt):
     * - Khách hàng chưa thanh toán hoặc chỉ mới thanh toán một phần
     *   (Số tiền thực thu < Tổng giá trị đơn hàng).
     * - Đơn hàng này sẽ tự động được đưa vào Sổ theo dõi công nợ để thu hồi sau.
     */
    DEBT
}

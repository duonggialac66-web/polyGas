package poly.duantn.entity.enums;

/**
 * Vai trò / Quyền hạn của tài khoản trong hệ thống OriginGas Enterprise
 */
public enum Role {

    /**
     * Quản trị viên (Admin):
     * - Toàn quyền quản trị hệ thống.
     * - Xem Dashboard tổng hợp tài chính, sản lượng, quản lý kho hàng,
     *   danh mục sản phẩm, nhân sự, cấu hình hoa hồng và chốt lương tháng.
     */
    ADMIN,

    /**
     * Nhân viên giao hàng (Employee / Shipper):
     * - Thao tác trên thiết bị di động.
     * - Lập báo cáo giao gas, quản lý thông tin khách hàng, ghim tọa độ GPS,
     *   cập nhật thu hồi nợ và ghi nhận các chi phí phát sinh hàng ngày.
     */
    EMPLOYEE
}

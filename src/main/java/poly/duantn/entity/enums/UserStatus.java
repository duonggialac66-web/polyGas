package poly.duantn.entity.enums;

/**
 * Trạng thái hoạt động của tài khoản người dùng / nhân viên
 */
public enum UserStatus {

    /**
     * Đang hoạt động (Active):
     * - Nhân viên đang làm việc bình thường.
     * - Được phép đăng nhập và thực hiện các nghiệp vụ trong hệ thống.
     */
    ACTIVE,

    /**
     * Ngừng hoạt động / Tạm khóa (Inactive):
     * - Nhân viên đã nghỉ việc hoặc tài khoản bị vô hiệu hóa tạm thời.
     * - Hệ thống chặn đăng nhập ngay lập tức nhưng vẫn bảo lưu toàn bộ
     *   lịch sử đơn hàng, chi phí và doanh số đã thực hiện trong quá khứ.
     */
    INACTIVE
}

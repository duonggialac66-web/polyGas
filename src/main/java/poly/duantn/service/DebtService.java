package poly.duantn.service;

import poly.duantn.dto.DebtResponse;
import poly.duantn.dto.PayDebtRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ Sổ nợ khách hàng
 */
public interface DebtService {

    /**
     * Lấy danh sách toàn bộ các đơn hàng đang bị nợ (payment_status = 'DEBT')
     * Có thể lọc theo tên/SĐT khách hàng hoặc theo nhân viên phụ trách (shipper).
     *
     * @param customer Từ khóa tên hoặc số điện thoại khách hàng (tùy chọn)
     * @param employeeId ID nhân viên giao hàng (tùy chọn)
     * @return Danh sách các khoản nợ kèm số nợ còn lại
     */
    List<DebtResponse> getDebts(String customer, UUID employeeId);

    /**
     * Thu nợ từ khách hàng
     * Cộng dồn số tiền thu vào actualReceived. Nếu actualReceived >= total thì tự động đổi sang PAID.
     *
     * @param reportId ID phiếu báo cáo giao hàng bị nợ
     * @param request Yêu cầu thu nợ (số tiền khách trả thêm)
     * @return Thông tin cập nhật của khoản nợ
     */
    DebtResponse payDebt(UUID reportId, PayDebtRequest request);
}

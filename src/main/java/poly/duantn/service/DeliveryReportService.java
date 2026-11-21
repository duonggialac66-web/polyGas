package poly.duantn.service;

import poly.duantn.dto.DailyReconciliationResponse;
import poly.duantn.dto.DeliveryReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ Lịch sử giao gas & Đối soát tiền mặt cuối ngày
 */
public interface DeliveryReportService {

    /**
     * Lấy lịch sử giao hàng theo khoảng ngày (fromDate -> toDate) của shipper hoặc toàn hệ thống.
     *
     * @param fromDate Ngày bắt đầu (mặc định 30 ngày trước nếu null)
     * @param toDate Ngày kết thúc (mặc định hôm nay nếu null)
     * @param employeeId ID shipper (tùy chọn, null nếu xem toàn hệ thống)
     * @return Danh sách báo cáo giao hàng sắp xếp giảm dần theo thời gian
     */
    List<DeliveryReportResponse> getDeliveryHistory(LocalDate fromDate, LocalDate toDate, UUID employeeId);

    /**
     * Lấy số liệu đối soát nộp tiền mặt cuối ngày của shipper
     * Sử dụng Java Stream tính tổng actualReceived để biết shipper phải bàn giao bao nhiêu tiền mặt.
     *
     * @param reportDate Ngày đối soát (mặc định là hôm nay)
     * @param employeeId ID shipper đối soát (tùy chọn)
     * @return Số liệu thống kê đối soát cuối ca
     */
    DailyReconciliationResponse getDailyReconciliation(LocalDate reportDate, UUID employeeId);
}

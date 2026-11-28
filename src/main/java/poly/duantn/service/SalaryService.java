package poly.duantn.service;

import poly.duantn.dto.SalaryResultResponse;
import poly.duantn.dto.UpdateSalaryInputRequest;
import poly.duantn.entity.SalaryConfig;
import poly.duantn.entity.SalaryFormula;

import java.util.List;
import java.util.UUID;

/**
 * Service Quản lý & Tính Lương SpEL Engine (Phân hệ Hà Trọng Phúc - PC11019)
 */
public interface SalaryService {

    /**
     * Chạy thuật toán tính lương tháng cho nhân viên theo tháng chỉ định (YYYY-MM)
     * Áp dụng SpEL Engine: (#baseSalary / 26 * #workDays) + (#commissionTotal * #overtime) + #bonus
     *
     * @param month Tháng cần tính lương định dạng "YYYY-MM" (ví dụ: "2026-11")
     * @param employeeId ID nhân viên cụ thể, nếu null thì tính cho toàn bộ nhân viên ACTIVE
     * @return Danh sách bảng lương tháng chi tiết sau khi tính
     */
    List<SalaryResultResponse> calculateMonthlySalaries(String month, UUID employeeId);

    /**
     * Lấy lịch sử bảng lương các tháng trước
     *
     * @param month Tháng cần xem bảng lương (ví dụ: "2026-11")
     */
    List<SalaryResultResponse> getSalaryHistory(String month);

    /**
     * Cập nhật biến số chấm công (workDays, overtime, bonus) cho nhân viên
     */
    SalaryResultResponse updateSalaryInput(UUID employeeId, String month, UpdateSalaryInputRequest request);

    /**
     * Lấy công thức tính lương SpEL đang được kích hoạt hiệu lực
     */
    SalaryFormula getActiveFormula();

    /**
     * Lấy danh sách cấu hình đơn giá hoa hồng gas
     */
    List<SalaryConfig> getSalaryConfigs();
}

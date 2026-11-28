package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.SalaryResultResponse;
import poly.duantn.dto.UpdateSalaryInputRequest;
import poly.duantn.entity.SalaryConfig;
import poly.duantn.entity.SalaryFormula;
import poly.duantn.service.SalaryService;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller cho Quản lý & Tính Lương SpEL Engine (/api/v1/salaries)
 * Phân hệ Hà Trọng Phúc - PC11019
 */
@RestController
@RequestMapping("/api/v1/salaries")
@RequiredArgsConstructor
@Slf4j
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * Chạy thuật toán tính lương SpEL Engine cho nhân viên theo tháng
     * POST /api/v1/salaries/calculate?month=YYYY-MM&employeeId=...
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<List<SalaryResultResponse>>> calculateSalary(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) UUID employeeId
    ) {
        String targetMonth = (month != null && !month.trim().isEmpty()) 
                ? month.trim() 
                : YearMonth.now().toString();

        log.info("API POST /api/v1/salaries/calculate - tháng: {}, employeeId: {}", targetMonth, employeeId);
        List<SalaryResultResponse> results = salaryService.calculateMonthlySalaries(targetMonth, employeeId);
        return ResponseEntity.ok(ApiResponse.ok(
                results, 
                "Tính lương SpEL thành công cho kỳ " + targetMonth + " (" + results.size() + " nhân viên)"
        ));
    }

    /**
     * Xem lịch sử bảng lương các tháng trước
     * GET /api/v1/salaries/history?month=YYYY-MM
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SalaryResultResponse>>> getSalaryHistory(
            @RequestParam(required = false) String month
    ) {
        String targetMonth = (month != null && !month.trim().isEmpty()) 
                ? month.trim() 
                : YearMonth.now().toString();

        log.info("API GET /api/v1/salaries/history - tháng: {}", targetMonth);
        List<SalaryResultResponse> history = salaryService.getSalaryHistory(targetMonth);
        return ResponseEntity.ok(ApiResponse.ok(history, "Lấy lịch sử bảng lương tháng " + targetMonth + " thành công"));
    }

    /**
     * Cập nhật biến số chấm công (workDays, overtime, bonus) cho nhân viên theo tháng
     * PUT /api/v1/salaries/{employeeId}/{month}
     */
    @PutMapping("/{employeeId}/{month}")
    public ResponseEntity<ApiResponse<SalaryResultResponse>> updateSalaryInput(
            @PathVariable UUID employeeId,
            @PathVariable String month,
            @Valid @RequestBody UpdateSalaryInputRequest request
    ) {
        log.info("API PUT /api/v1/salaries/{}/{} - workDays: {}, overtime: {}, bonus: {}",
                employeeId, month, request.getWorkDays(), request.getOvertime(), request.getBonus());

        SalaryResultResponse updated = salaryService.updateSalaryInput(employeeId, month, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Cập nhật biến số chấm công và tính lại lương thành công"));
    }

    /**
     * Lấy công thức tính lương SpEL đang được kích hoạt hiệu lực
     * GET /api/v1/salaries/formula
     */
    @GetMapping("/formula")
    public ResponseEntity<ApiResponse<SalaryFormula>> getActiveFormula() {
        log.info("API GET /api/v1/salaries/formula");
        SalaryFormula formula = salaryService.getActiveFormula();
        return ResponseEntity.ok(ApiResponse.ok(formula, "Lấy công thức tính lương chuẩn thành công"));
    }

    /**
     * Lấy danh sách cấu hình hoa hồng theo loại bình gas
     * GET /api/v1/salaries/configs
     */
    @GetMapping("/configs")
    public ResponseEntity<ApiResponse<List<SalaryConfig>>> getSalaryConfigs() {
        log.info("API GET /api/v1/salaries/configs");
        List<SalaryConfig> configs = salaryService.getSalaryConfigs();
        return ResponseEntity.ok(ApiResponse.ok(configs, "Lấy danh sách cấu hình hoa hồng gas thành công"));
    }
}

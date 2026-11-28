package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.SalaryResultResponse;
import poly.duantn.dto.UpdateSalaryInputRequest;
import poly.duantn.entity.*;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.*;
import poly.duantn.service.SalaryService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cài đặt chi tiết bộ máy tính lương SpEL Engine (Phân hệ Hà Trọng Phúc - PC11019)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalaryServiceImpl implements SalaryService {

    private final MonthlySalaryInputRepository monthlySalaryInputRepository;
    private final SalaryFormulaRepository salaryFormulaRepository;
    private final SalaryConfigRepository salaryConfigRepository;
    private final DeliveryReportRepository deliveryReportRepository;
    private final UserRepository userRepository;

    private final ExpressionParser spelParser = new SpelExpressionParser();

    @Override
    public List<SalaryResultResponse> calculateMonthlySalaries(String month, UUID employeeId) {
        log.info("Khởi chạy tính lương SpEL cho tháng: {}, employeeId: {}", month, employeeId);
        validateMonth(month);

        YearMonth ym = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        // 1. Lấy công thức lương chuẩn đang kích hoạt hiệu lực
        SalaryFormula formula = salaryFormulaRepository.findByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công thức tính lương SpEL đang kích hoạt"));

        // 2. Lấy danh sách nhân sự cần tính lương
        List<User> targetEmployees;
        if (employeeId != null) {
            User emp = userRepository.findById(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", employeeId));
            targetEmployees = Collections.singletonList(emp);
        } else {
            targetEmployees = userRepository.findByStatus(UserStatus.ACTIVE);
        }

        // 3. Tải danh sách cấu hình hoa hồng theo loại bình gas
        List<SalaryConfig> salaryConfigs = salaryConfigRepository.findAll();

        List<SalaryResultResponse> results = new ArrayList<>();

        for (User emp : targetEmployees) {
            // Lấy hoặc tạo mới biến số chấm công cho nhân viên trong tháng
            MonthlySalaryInput salaryInput = monthlySalaryInputRepository
                    .findByEmployeeIdAndMonth(emp.getId(), month)
                    .orElseGet(() -> MonthlySalaryInput.builder()
                            .employee(emp)
                            .month(month)
                            .workDays(26.0)
                            .overtime(1.0)
                            .bonus(0.0)
                            .calculatedSalary(0.0)
                            .build());

            // 4. Tính tổng hoa hồng (#commissionTotal) và tổng sản lượng bình shipper giao
            CommissionCalculation commissionResult = calculateCommissionForEmployee(
                    emp.getId(), startDate, endDate, salaryConfigs
            );

            // 5. Đánh giá biểu thức SpEL Engine
            double baseSalary = (emp.getBaseSalary() != null) ? emp.getBaseSalary() : 0.0;
            double workDays = (salaryInput.getWorkDays() != null) ? salaryInput.getWorkDays() : 26.0;
            double overtime = (salaryInput.getOvertime() != null) ? salaryInput.getOvertime() : 1.0;
            double bonus = (salaryInput.getBonus() != null) ? salaryInput.getBonus() : 0.0;
            double commissionTotal = commissionResult.totalCommission;

            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("baseSalary", baseSalary);
            context.setVariable("workDays", workDays);
            context.setVariable("overtime", overtime);
            context.setVariable("bonus", bonus);
            context.setVariable("commissionTotal", commissionTotal);

            double calculatedSalary;
            try {
                Number evalResult = spelParser.parseExpression(formula.getFormulaExpression())
                        .getValue(context, Number.class);
                calculatedSalary = (evalResult != null) ? evalResult.doubleValue() : 0.0;
            } catch (Exception ex) {
                log.warn("Lỗi tính SpEL cho {}, dùng công thức mặc định: {}", emp.getUsername(), ex.getMessage());
                calculatedSalary = (baseSalary / 26.0 * workDays) + (commissionTotal * overtime) + bonus;
            }

            calculatedSalary = Math.round(calculatedSalary * 100.0) / 100.0;

            // 6. Lưu vết lịch sử (Audit Trail) vào bảng monthly_salary_inputs
            salaryInput.setSalaryFormula(formula);
            salaryInput.setCalculatedSalary(calculatedSalary);
            MonthlySalaryInput savedInput = monthlySalaryInputRepository.save(salaryInput);

            // 7. Chuyển đổi DTO trả về cho Controller
            results.add(SalaryResultResponse.builder()
                    .id(savedInput.getId())
                    .employeeId(emp.getId())
                    .employeeName(emp.getName())
                    .username(emp.getUsername())
                    .phone(emp.getPhone())
                    .role(emp.getRole())
                    .month(month)
                    .baseSalary(baseSalary)
                    .workDays(workDays)
                    .overtime(overtime)
                    .bonus(bonus)
                    .deliveredCylinders(commissionResult.totalCylinders)
                    .commissionTotal(commissionTotal)
                    .calculatedSalary(calculatedSalary)
                    .formulaName(formula.getName())
                    .formulaExpression(formula.getFormulaExpression())
                    .status("CALCULATED")
                    .updatedAt(savedInput.getUpdatedAt())
                    .build());
        }

        log.info("Đã hoàn tất tính lương SpEL cho {} nhân viên trong tháng {}", results.size(), month);
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryResultResponse> getSalaryHistory(String month) {
        log.info("Truy vấn lịch sử bảng lương tháng: {}", month);
        validateMonth(month);

        List<MonthlySalaryInput> inputs = monthlySalaryInputRepository.findByMonth(month);
        if (inputs.isEmpty()) {
            return Collections.emptyList();
        }

        YearMonth ym = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        List<SalaryConfig> salaryConfigs = salaryConfigRepository.findAll();

        return inputs.stream().map(input -> {
            User emp = input.getEmployee();
            CommissionCalculation comm = calculateCommissionForEmployee(
                    emp.getId(), startDate, endDate, salaryConfigs
            );

            return SalaryResultResponse.builder()
                    .id(input.getId())
                    .employeeId(emp.getId())
                    .employeeName(emp.getName())
                    .username(emp.getUsername())
                    .phone(emp.getPhone())
                    .role(emp.getRole())
                    .month(input.getMonth())
                    .baseSalary(emp.getBaseSalary())
                    .workDays(input.getWorkDays())
                    .overtime(input.getOvertime())
                    .bonus(input.getBonus())
                    .deliveredCylinders(comm.totalCylinders)
                    .commissionTotal(comm.totalCommission)
                    .calculatedSalary(input.getCalculatedSalary())
                    .formulaName(input.getSalaryFormula() != null ? input.getSalaryFormula().getName() : "Mặc định")
                    .formulaExpression(input.getSalaryFormula() != null ? input.getSalaryFormula().getFormulaExpression() : "")
                    .status("CALCULATED")
                    .updatedAt(input.getUpdatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public SalaryResultResponse updateSalaryInput(UUID employeeId, String month, UpdateSalaryInputRequest request) {
        log.info("Cập nhật biến số chấm công cho employeeId: {}, month: {}", employeeId, month);
        validateMonth(month);

        User emp = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", employeeId));

        MonthlySalaryInput input = monthlySalaryInputRepository
                .findByEmployeeIdAndMonth(employeeId, month)
                .orElseGet(() -> MonthlySalaryInput.builder()
                        .employee(emp)
                        .month(month)
                        .workDays(26.0)
                        .overtime(1.0)
                        .bonus(0.0)
                        .calculatedSalary(0.0)
                        .build());

        if (request.getWorkDays() != null) input.setWorkDays(request.getWorkDays());
        if (request.getOvertime() != null) input.setOvertime(request.getOvertime());
        if (request.getBonus() != null) input.setBonus(request.getBonus());

        // Tự động kích hoạt tính lại lương cho nhân viên này sau khi cập nhật biến số
        List<SalaryResultResponse> updatedList = calculateMonthlySalaries(month, employeeId);
        return updatedList.isEmpty() ? null : updatedList.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryFormula getActiveFormula() {
        return salaryFormulaRepository.findByIsActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công thức tính lương SpEL đang kích hoạt"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryConfig> getSalaryConfigs() {
        return salaryConfigRepository.findAll();
    }

    /**
     * Hàm phụ trợ tính tổng sản lượng bình gas và hoa hồng giao gas của nhân viên trong tháng
     */
    private CommissionCalculation calculateCommissionForEmployee(
            UUID employeeId, LocalDate startDate, LocalDate endDate, List<SalaryConfig> configs
    ) {
        List<DeliveryReport> reports = deliveryReportRepository
                .findByEmployeeIdAndReportDateBetween(employeeId, startDate, endDate);

        Map<UUID, Integer> quantityByInventory = new HashMap<>();
        int totalCylinders = 0;

        for (DeliveryReport report : reports) {
            if (report.getInventory() != null && report.getQuantity() != null) {
                quantityByInventory.merge(report.getInventory().getId(), report.getQuantity(), Integer::sum);
                totalCylinders += report.getQuantity();
            }
        }

        double totalCommission = 0.0;
        for (SalaryConfig config : configs) {
            if (config.getInventory() != null) {
                int qty = quantityByInventory.getOrDefault(config.getInventory().getId(), 0);
                if (qty > 0) {
                    double unitRate = (config.getCommissionPerUnit() != null) ? config.getCommissionPerUnit() : 0.0;
                    totalCommission += qty * unitRate;

                    // Thưởng đạt mốc số lượng bình giao (Milestone Bonus)
                    if (config.getMilestoneBonusThreshold() != null && config.getMilestoneBonusAmount() != null) {
                        if (qty >= config.getMilestoneBonusThreshold()) {
                            totalCommission += config.getMilestoneBonusAmount();
                        }
                    }
                }
            }
        }

        return new CommissionCalculation(totalCylinders, totalCommission);
    }

    private void validateMonth(String month) {
        if (month == null || month.trim().isEmpty()) {
            throw new BadRequestException("Tháng tính lương không được để trống (định dạng YYYY-MM)");
        }
        try {
            YearMonth.parse(month.trim());
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Định dạng tháng không hợp lệ (yêu cầu YYYY-MM, ví dụ: 2026-11)");
        }
    }

    private static class CommissionCalculation {
        final int totalCylinders;
        final double totalCommission;

        CommissionCalculation(int totalCylinders, double totalCommission) {
            this.totalCylinders = totalCylinders;
            this.totalCommission = totalCommission;
        }
    }
}

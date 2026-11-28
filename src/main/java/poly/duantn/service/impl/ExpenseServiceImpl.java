package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.ExpenseRequest;
import poly.duantn.dto.ExpenseResponse;
import poly.duantn.entity.Expense;
import poly.duantn.entity.User;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.ExpenseRepository;
import poly.duantn.repository.UserRepository;
import poly.duantn.service.ExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cài đặt chi tiết nghiệp vụ Chi phí Vận hành (Expense Module)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Số tiền chi phí phải lớn hơn 0");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", request.getEmployeeId()));

        Expense expense = Expense.builder()
                .employee(employee)
                .expenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDate.now())
                .description(request.getDescription().trim())
                .amount(request.getAmount())
                .notes(request.getNotes())
                .build();

        Expense saved = expenseRepository.save(expense);
        log.info("Ghi nhận chi phí thành công: ID={}, Employee={}, Amount={}",
                saved.getId(), employee.getUsername(), saved.getAmount());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getDailyExpenses(UUID employeeId, LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        log.info("Lấy chi phí ngày {} của nhân viên {}", queryDate, employeeId);

        return expenseRepository.findByEmployeeIdAndExpenseDate(employeeId, queryDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByDate(LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        log.info("Lấy toàn bộ chi phí ngày {}", queryDate);

        return expenseRepository.findByExpenseDate(queryDate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .employeeId(expense.getEmployee() != null ? expense.getEmployee().getId() : null)
                .employeeName(expense.getEmployee() != null ? expense.getEmployee().getName() : null)
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .notes(expense.getNotes())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}

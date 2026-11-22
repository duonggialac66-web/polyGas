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

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {
        if (request.getEmployeeId() == null) {
            throw new BadRequestException("employeeId không được để trống");
        }
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Số tiền chi phí (amount) phải lớn hơn 0");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Mô tả chi phí không được để trống");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("User (Nhân viên)", "id", request.getEmployeeId()));

        LocalDate date = request.getExpenseDate() != null ? request.getExpenseDate() : LocalDate.now();

        Expense expense = Expense.builder()
                .employee(employee)
                .expenseDate(date)
                .description(request.getDescription().trim())
                .amount(request.getAmount())
                .notes(request.getNotes())
                .build();

        Expense saved = expenseRepository.save(expense);
        log.info("Recorded expense id={}, employeeId={}, amount={}, date={}",
                saved.getId(), employee.getId(), saved.getAmount(), saved.getExpenseDate());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getDailyExpenses(UUID employeeId, LocalDate date) {
        if (employeeId == null) {
            throw new BadRequestException("employeeId không được để trống");
        }
        LocalDate targetDate = date != null ? date : LocalDate.now();

        // Kiểm tra nhân viên tồn tại
        if (!userRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("User (Nhân viên)", "id", employeeId);
        }

        List<Expense> expenses = expenseRepository.findByEmployeeIdAndExpenseDate(employeeId, targetDate);
        return expenses.stream()
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

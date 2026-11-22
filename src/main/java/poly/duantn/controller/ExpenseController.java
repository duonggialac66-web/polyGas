package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.ExpenseRequest;
import poly.duantn.dto.ExpenseResponse;
import poly.duantn.service.ExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller cho Quản lý Chi phí Vận hành (/api/v1/expenses)
 */
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Shipper ghi nhận chi phí phát sinh trong ngày (xăng xe, ăn trưa... amount > 0)
     * POST /api/v1/expenses
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseRequest request
    ) {
        log.info("API POST /api/v1/expenses - Ghi nhận chi phí cho nhân viên: {}, số tiền: {}",
                request != null ? request.getEmployeeId() : null,
                request != null ? request.getAmount() : null);
        ExpenseResponse created = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Ghi nhận chi phí thành công"));
    }

    /**
     * Xem các khoản chi trong ngày của shipper
     * GET /api/v1/expenses hoặc GET /api/v1/expenses/daily
     */
    @GetMapping({"", "/daily"})
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getDailyExpenses(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("API GET /api/v1/expenses - employeeId: {}, date: {}", employeeId, date);
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<ExpenseResponse> expenses = (employeeId != null) 
                ? expenseService.getDailyExpenses(employeeId, queryDate)
                : expenseService.getExpensesByDate(queryDate);
        return ResponseEntity.ok(ApiResponse.ok(expenses, "Lấy danh sách chi phí trong ngày thành công"));
    }
}

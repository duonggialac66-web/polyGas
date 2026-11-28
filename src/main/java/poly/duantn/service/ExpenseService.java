package poly.duantn.service;

import poly.duantn.dto.ExpenseRequest;
import poly.duantn.dto.ExpenseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {

    /**
     * Ghi nhận chi phí phát sinh trong ngày
     */
    ExpenseResponse createExpense(ExpenseRequest request);

    /**
     * Xem các khoản chi trong ngày của shipper/nhân viên
     */
    List<ExpenseResponse> getDailyExpenses(UUID employeeId, LocalDate date);

    /**
     * Lấy toàn bộ chi phí theo ngày (hỗ trợ lọc khi không truyền employeeId)
     */
    List<ExpenseResponse> getExpensesByDate(LocalDate date);
}
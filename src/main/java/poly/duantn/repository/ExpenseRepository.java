package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    /**
     * Lấy danh sách các khoản chi của nhân viên trong ngày (tối ưu qua Index IX_Expenses_Employee_Date)
     */
    List<Expense> findByEmployeeIdAndExpenseDate(UUID employeeId, LocalDate expenseDate);

    /**
     * Lấy danh sách chi phí của nhân viên trong khoảng thời gian
     */
    List<Expense> findByEmployeeIdAndExpenseDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * Lọc toàn bộ chi phí phát sinh của toàn công ty theo khoảng thời gian
     */
    List<Expense> findByExpenseDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Tính tổng chi phí phát sinh của nhân viên trong ngày (phục vụ đối soát tiền nộp cuối ca)
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e " +
           "WHERE e.employee.id = :employeeId AND e.expenseDate = :expenseDate")
    Double sumAmountByEmployeeAndDate(@Param("employeeId") UUID employeeId,
                                      @Param("expenseDate") LocalDate expenseDate);
}

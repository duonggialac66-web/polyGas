package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.MonthlySalaryInput;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonthlySalaryInputRepository extends JpaRepository<MonthlySalaryInput, UUID> {

    /**
     * Tìm thông tin bảng lương của nhân viên theo tháng (ví dụ: "2026-11")
     */
    Optional<MonthlySalaryInput> findByEmployeeIdAndMonth(UUID employeeId, String month);

    /**
     * Lấy toàn bộ bảng lương của tất cả nhân viên trong một tháng
     */
    List<MonthlySalaryInput> findByMonth(String month);

    /**
     * Kiểm tra tháng này nhân viên đã được chốt lương hay chưa
     */
    boolean existsByEmployeeIdAndMonth(UUID employeeId, String month);
}


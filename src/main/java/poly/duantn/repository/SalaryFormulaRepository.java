package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.SalaryFormula;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryFormulaRepository extends JpaRepository<SalaryFormula, UUID> {

    /**
     * Lấy công thức tính lương SpEL đang được kích hoạt hiệu lực
     */
    Optional<SalaryFormula> findByIsActiveTrue();

    /**
     * Lấy danh sách lịch sử tất cả công thức tính lương sắp xếp theo ngày áp dụng mới nhất
     */
    List<SalaryFormula> findByOrderByEffectiveDateDesc();
}


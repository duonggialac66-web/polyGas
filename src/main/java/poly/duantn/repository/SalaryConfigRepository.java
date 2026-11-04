package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.SalaryConfig;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, UUID> {

    /**
     * Tìm cấu hình đơn giá hoa hồng theo loại bình gas
     */
    Optional<SalaryConfig> findByContainerType(String containerType);

    /**
     * Kiểm tra loại bình đã có cấu hình hoa hồng chưa
     */
    boolean existsByContainerType(String containerType);
}

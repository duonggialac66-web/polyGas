package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.SalaryConfig;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaryConfigRepository extends JpaRepository<SalaryConfig, UUID> {

    /**
     * Tìm cấu hình hoa hồng theo khóa ngoại inventory_id
     */
    Optional<SalaryConfig> findByInventoryId(UUID inventoryId);

    /**
     * Tìm cấu hình hoa hồng theo tên loại bình thông qua khóa ngoại liên kết
     */
    Optional<SalaryConfig> findByInventoryContainerType(String containerType);

    /**
     * Kiểm tra loại bình đã có cấu hình hoa hồng chưa
     */
    boolean existsByInventoryId(UUID inventoryId);
}

package poly.duantn.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /**
     * Tìm thông tin tồn kho theo loại bình gas
     */
    Optional<Inventory> findByContainerType(String containerType);

    /**
     * Kiểm tra loại bình đã có trong kho chưa
     */
    boolean existsByContainerType(String containerType);

    /**
     * KHÓA BI QUAN (Pessimistic Write Lock) trên MS SQL Server:
     * Tương đương lệnh: SELECT ... WITH (UPDLOCK)
     * Ngăn ngừa hoàn toàn hiện tượng âm kho và Race Condition khi nhiều nhân viên tạo đơn cùng lúc.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.containerType = :containerType")
    Optional<Inventory> findByContainerTypeWithLock(@Param("containerType") String containerType);

    /**
     * Lấy danh sách các loại bình có số lượng đầy dưới ngưỡng an toàn (cảnh báo nhập kho)
     */
    @Query("SELECT i FROM Inventory i WHERE i.fullQuantity <= i.warningThreshold")
    List<Inventory> findLowStockInventories();
}

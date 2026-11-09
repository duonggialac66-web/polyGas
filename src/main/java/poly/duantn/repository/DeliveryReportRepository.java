package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.DeliveryReport;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryReportRepository extends JpaRepository<DeliveryReport, UUID> {

    /**
     * Lấy danh sách báo cáo giao gas của nhân viên trong ngày (tối ưu qua Index IX_DeliveryReports_Employee_Date)
     */
    List<DeliveryReport> findByEmployeeIdAndReportDate(UUID employeeId, LocalDate reportDate);

    /**
     * Lấy danh sách đơn theo trạng thái thanh toán (PAID hoặc DEBT)
     */
    List<DeliveryReport> findByPaymentStatus(PaymentStatus paymentStatus);

    /**
     * Lấy các đơn nợ (DEBT) của một khách hàng cụ thể
     */
    List<DeliveryReport> findByCustomerIdAndPaymentStatus(UUID customerId, PaymentStatus paymentStatus);

    /**
     * Lọc báo cáo giao hàng theo loại bình trong kho qua khóa ngoại inventory_id
     */
    List<DeliveryReport> findByInventoryId(UUID inventoryId);

    /**
     * Lọc báo cáo giao hàng trong khoảng thời gian
     */
    List<DeliveryReport> findByReportDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Lọc báo cáo của nhân viên trong khoảng thời gian (dùng tổng hợp tháng)
     */
    List<DeliveryReport> findByEmployeeIdAndReportDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    /**
     * Tính tổng số tiền thực thu của nhân viên trong ngày (phục vụ đối soát tiền nộp cuối ca)
     */
    @Query("SELECT COALESCE(SUM(d.actualReceived), 0.0) FROM DeliveryReport d " +
           "WHERE d.employee.id = :employeeId AND d.reportDate = :reportDate")
    Double sumActualReceivedByEmployeeAndDate(@Param("employeeId") UUID employeeId,
                                             @Param("reportDate") LocalDate reportDate);

    /**
     * Đếm tổng số bình gas giao thành công theo loại bình của nhân viên trong tháng qua khóa ngoại d.inventory
     */
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM DeliveryReport d " +
           "WHERE d.employee.id = :employeeId " +
           "AND d.inventory.containerType = :containerType " +
           "AND d.reportDate >= :startDate AND d.reportDate <= :endDate")
    Integer sumQuantityByEmployeeAndContainerTypeAndDateRange(@Param("employeeId") UUID employeeId,
                                                             @Param("containerType") String containerType,
                                                             @Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);
}

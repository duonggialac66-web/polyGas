package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * Tra cứu khách hàng siêu tốc O(1) qua mã băm chuẩn hóa tên
     */
    Optional<Customer> findByNameHash(String nameHash);

    /**
     * Kiểm tra tên khách hàng đã tồn tại hay chưa (chống tạo trùng)
     */
    boolean existsByNameHash(String nameHash);

    /**
     * Tìm khách hàng theo số điện thoại
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Tìm kiếm khách hàng theo tên (hỗ trợ tìm gần đúng)
     */
    List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * Tìm kiếm khách hàng theo số điện thoại hoặc địa chỉ
     */
    List<Customer> findByPhoneContainingOrAddressContainingIgnoreCase(String phone, String address);
}

package poly.duantn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Tìm người dùng theo tên đăng nhập (dùng cho xác thực đăng nhập Spring Security)
     */
    Optional<User> findByUsername(String username);

    /**
     * Kiểm tra username đã tồn tại hay chưa
     */
    boolean existsByUsername(String username);

    /**
     * Lọc danh sách người dùng theo vai trò (ADMIN hoặc EMPLOYEE)
     */
    List<User> findByRole(Role role);

    /**
     * Lọc danh sách người dùng theo trạng thái (ACTIVE hoặc INACTIVE)
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Tìm nhân viên theo số điện thoại
     */
    Optional<User> findByPhone(String phone);
}


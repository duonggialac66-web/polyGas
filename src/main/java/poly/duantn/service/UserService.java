package poly.duantn.service;

import poly.duantn.dto.CreateEmployeeRequest;
import poly.duantn.dto.UserRequest;
import poly.duantn.dto.UserResponse;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;

import java.util.List;
import java.util.UUID;

/**
 * Service quản lý Nhân sự & Người dùng (Phân hệ Hà Trọng Phúc - PC11019)
 */
public interface UserService {

    /**
     * Lấy danh sách nhân viên có hỗ trợ tìm kiếm và lọc
     */
    List<UserResponse> getAllUsers(Role role, UserStatus status, String keyword);

    /**
     * Lấy chi tiết nhân viên theo ID
     */
    UserResponse getUserById(UUID id);

    /**
     * Thêm mới nhân viên qua DTO CreateEmployeeRequest chuẩn hóa
     */
    UserResponse createEmployee(CreateEmployeeRequest request);

    /**
     * Thêm mới nhân viên từ form UserRequest
     */
    UserResponse createUser(UserRequest request);

    /**
     * Cập nhật thông tin nhân viên
     */
    UserResponse updateUser(UUID id, UserRequest request);

    /**
     * Bật / Tắt trạng thái làm việc (ACTIVE <-> INACTIVE)
     */
    UserResponse toggleStatus(UUID id);

    /**
     * Xóa nhân viên theo ID
     */
    void deleteUser(UUID id);
}

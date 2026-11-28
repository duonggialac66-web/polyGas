package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.CreateEmployeeRequest;
import poly.duantn.dto.UserRequest;
import poly.duantn.dto.UserResponse;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller quản lý Nhân viên & Người dùng (Employment / User Management)
 * Phân hệ Hà Trọng Phúc - PC11019
 */
@RestController
@RequestMapping({ "/api/v1/users", "/api/v1/employees" })
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Lấy danh sách nhân viên có hỗ trợ tìm kiếm và lọc
     * GET /api/v1/users hoặc GET /api/v1/employees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword) {
        log.info("API GET /api/v1/users - role: {}, status: {}, keyword: {}", role, status, keyword);
        List<UserResponse> responses = userService.getAllUsers(role, status, keyword);
        return ResponseEntity.ok(ApiResponse.ok(responses, "Lấy danh sách nhân viên thành công"));
    }

    /**
     * Lấy chi tiết một nhân viên theo ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        log.info("API GET /api/v1/users/{}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.ok(response, "Lấy thông tin nhân viên thành công"));
    }

    /**
     * Thêm mới nhân viên qua DTO CreateEmployeeRequest (có băm mật khẩu BCrypt)
     * POST /api/v1/users/employee
     */
    @PostMapping("/employee")
    public ResponseEntity<ApiResponse<UserResponse>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        log.info("API POST /api/v1/users/employee - username: {}", request.getUsername());
        UserResponse created = userService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Thêm nhân viên mới thành công"));
    }

    /**
     * Thêm mới người dùng / nhân viên tổng quát
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        log.info("API POST /api/v1/users - username: {}", request.getUsername());
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Tạo người dùng thành công"));
    }

    /**
     * Cập nhật thông tin nhân viên
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequest request) {
        log.info("API PUT /api/v1/users/{}", id);
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Cập nhật thông tin nhân viên thành công"));
    }

    /**
     * Đổi nhanh trạng thái nhân viên (ACTIVE <-> INACTIVE)
     * PATCH /api/v1/users/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable UUID id) {
        log.info("API PATCH /api/v1/users/{}/toggle-status", id);
        UserResponse updated = userService.toggleStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(
                updated,
                "Đã chuyển trạng thái nhân viên thành: " + updated.getStatus()
        ));
    }

    /**
     * Xóa nhân viên
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        log.info("API DELETE /api/v1/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok(null, "Xóa nhân viên thành công"));
    }
}

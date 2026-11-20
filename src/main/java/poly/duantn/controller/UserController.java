package poly.duantn.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.UserRequest;
import poly.duantn.dto.UserResponse;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller quản lý Nhân viên & Người dùng (Employment / User Management)
 * Cung cấp đầy đủ API RESTful cho Frontend VueJS điều hướng và quản lý.
 */
@RestController
@RequestMapping({"/api/v1/users", "/api/v1/employees"})
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách nhân viên có hỗ trợ tìm kiếm và lọc
     * GET /api/v1/users hoặc GET /api/v1/employees
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String keyword
    ) {
        log.info("API GET users: role={}, status={}, keyword={}", role, status, keyword);
        List<User> users = userRepository.findAll();

        // Lọc theo role nếu có
        if (role != null) {
            users = users.stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
        }

        // Lọc theo status nếu có
        if (status != null) {
            users = users.stream()
                    .filter(u -> u.getStatus() == status)
                    .collect(Collectors.toList());
        }

        // Lọc theo từ khóa (tên, username, sđt) nếu có
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            users = users.stream()
                    .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(kw))
                            || (u.getUsername() != null && u.getUsername().toLowerCase().contains(kw))
                            || (u.getPhone() != null && u.getPhone().contains(kw)))
                    .collect(Collectors.toList());
        }

        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responses, "Lấy danh sách nhân viên thành công"));
    }

    /**
     * Lấy chi tiết một nhân viên theo ID
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(ApiResponse.ok(UserResponse.fromEntity(u))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy nhân viên với ID: " + id)));
    }

    /**
     * Thêm mới nhân viên
     * POST /api/v1/users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody UserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tên đăng nhập không được để trống"));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tên đăng nhập đã tồn tại trong hệ thống"));
        }

        String rawPassword = (request.getPassword() != null && !request.getPassword().trim().isEmpty())
                ? request.getPassword()
                : "123456";

        User user = User.builder()
                .name(request.getName() != null ? request.getName() : request.getUsername())
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(rawPassword))
                .role(request.getRole() != null ? request.getRole() : Role.EMPLOYEE)
                .phone(request.getPhone())
                .startDate(request.getStartDate())
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .baseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : 0.0)
                .avatarUrl(request.getAvatarUrl())
                .build();

        User saved = userRepository.save(user);
        log.info("Đã tạo mới nhân viên: {}", saved.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(UserResponse.fromEntity(saved), "Tạo nhân viên thành công"));
    }

    /**
     * Cập nhật thông tin nhân viên
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequest request
    ) {
        return userRepository.findById(id).map(user -> {
            if (request.getName() != null) user.setName(request.getName());
            if (request.getRole() != null) user.setRole(request.getRole());
            if (request.getPhone() != null) user.setPhone(request.getPhone());
            if (request.getStartDate() != null) user.setStartDate(request.getStartDate());
            if (request.getStatus() != null) user.setStatus(request.getStatus());
            if (request.getBaseSalary() != null) user.setBaseSalary(request.getBaseSalary());
            if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            User updated = userRepository.save(user);
            log.info("Đã cập nhật nhân viên: {}", updated.getUsername());
            return ResponseEntity.ok(ApiResponse.ok(UserResponse.fromEntity(updated), "Cập nhật thành công"));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Không tìm thấy nhân viên với ID: " + id)));
    }

    /**
     * Đổi nhanh trạng thái nhân viên (ACTIVE <-> INACTIVE)
     * PATCH /api/v1/users/{id}/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable UUID id) {
        return userRepository.findById(id).map(user -> {
            user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
            User updated = userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok(
                    UserResponse.fromEntity(updated),
                    "Đã chuyển trạng thái thành: " + updated.getStatus()
            ));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Không tìm thấy nhân viên với ID: " + id)));
    }

    /**
     * Xóa nhân viên theo ID
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            log.info("Đã xóa nhân viên ID: {}", id);
            return ResponseEntity.ok(ApiResponse.<Void>ok(null, "Xóa nhân viên thành công"));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>error("Không tìm thấy nhân viên với ID: " + id)));
    }
}


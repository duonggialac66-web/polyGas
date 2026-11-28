package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.CreateEmployeeRequest;
import poly.duantn.dto.UserRequest;
import poly.duantn.dto.UserResponse;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.UserRepository;
import poly.duantn.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cài đặt chi tiết nghiệp vụ Quản lý Nhân sự (Phân hệ Hà Trọng Phúc - PC11019)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(Role role, UserStatus status, String keyword) {
        log.info("Lấy danh sách nhân sự - role: {}, status: {}, keyword: {}", role, status, keyword);
        List<User> users = userRepository.findAll();

        if (role != null) {
            users = users.stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
        }

        if (status != null) {
            users = users.stream()
                    .filter(u -> u.getStatus() == status)
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            users = users.stream()
                    .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(kw))
                            || (u.getUsername() != null && u.getUsername().toLowerCase().contains(kw))
                            || (u.getPhone() != null && u.getPhone().contains(kw)))
                    .collect(Collectors.toList());
        }

        return users.stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));
        return UserResponse.fromEntity(user);
    }

    @Override
    public UserResponse createEmployee(CreateEmployeeRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập không được để trống");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new BadRequestException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại trong hệ thống");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.EMPLOYEE)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .startDate(request.getStartDate())
                .status(UserStatus.ACTIVE)
                .baseSalary(request.getBaseSalary() != null ? request.getBaseSalary() : 0.0)
                .build();

        User saved = userRepository.save(user);
        log.info("Thêm mới nhân viên thành công: username={}, role={}", saved.getUsername(), saved.getRole());
        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Tên đăng nhập không được để trống");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new BadRequestException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại trong hệ thống");
        }

        String rawPassword = (request.getPassword() != null && !request.getPassword().trim().isEmpty())
                ? request.getPassword()
                : "123456";

        User user = User.builder()
                .name(request.getName() != null ? request.getName().trim() : request.getUsername().trim())
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
        log.info("Tạo người dùng thành công: {}", saved.getUsername());
        return UserResponse.fromEntity(saved);
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));

        if (request.getName() != null) user.setName(request.getName().trim());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getPhone() != null) user.setPhone(request.getPhone().trim());
        if (request.getStartDate() != null) user.setStartDate(request.getStartDate());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        if (request.getBaseSalary() != null) user.setBaseSalary(request.getBaseSalary());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("Cập nhật nhân viên thành công: {}", updated.getUsername());
        return UserResponse.fromEntity(updated);
    }

    @Override
    public UserResponse toggleStatus(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));

        UserStatus newStatus = (user.getStatus() == UserStatus.ACTIVE) ? UserStatus.INACTIVE : UserStatus.ACTIVE;
        user.setStatus(newStatus);
        User updated = userRepository.save(user);
        log.info("Đổi trạng thái nhân viên {}: {}", updated.getUsername(), newStatus);
        return UserResponse.fromEntity(updated);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên", "id", id));
        userRepository.delete(user);
        log.info("Đã xóa nhân viên ID: {}", id);
    }
}

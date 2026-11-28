package poly.duantn.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.LoginRequest;
import poly.duantn.dto.LoginResponse;
import poly.duantn.dto.RegisterRequest;
import poly.duantn.dto.UserResponse;
import poly.duantn.entity.User;
import poly.duantn.entity.enums.Role;
import poly.duantn.entity.enums.UserStatus;
import poly.duantn.repository.UserRepository;
import poly.duantn.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller xác thực người dùng (Đăng nhập / Đăng ký)
 * Dùng JJWT 0.12.x để tạo JWT thật với HS256, hết hạn sau 24h.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

        /**
         * Đăng nhập
         * POST /api/v1/auth/login
         * Body: { "username": "admin", "password": "123456" }
         * Response: { token, username, name, role, expiresIn }
         */
        @PostMapping("/login")
        public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
                // Validate input
                if (request.getUsername() == null || request.getUsername().isBlank()) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error("Tên đăng nhập không được để trống"));
                }
                if (request.getPassword() == null || request.getPassword().isBlank()) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error("Mật khẩu không được để trống"));
                }

                // Tìm user
                Optional<User> userOpt = userRepository.findByUsername(request.getUsername().trim());
                if (userOpt.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không chính xác"));
                }

                User user = userOpt.get();

                // Kiểm tra mật khẩu
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không chính xác"));
                }

                // Kiểm tra trạng thái
                if (user.getStatus() == UserStatus.INACTIVE) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(ApiResponse.error(
                                                        "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên"));
                }

                // Tạo JWT thật — claims chứa username (subject), roles, userId, name
                Map<String, Object> claims = new HashMap<>();
                claims.put("roles", user.getRole().name());
                claims.put("userId", user.getId().toString());
                claims.put("name", user.getName());

                String token = jwtUtil.generateToken(claims, user.getUsername());

                LoginResponse response = LoginResponse.builder()
                                .token(token)
                                .username(user.getUsername())
                                .name(user.getName())
                                .role(user.getRole().name())
                                .expiresIn(jwtUtil.getExpirationMs())
                                .build();

                log.info("Đăng nhập thành công: {} ({})", user.getUsername(), user.getRole());
                return ResponseEntity.ok(ApiResponse.ok(response, "Đăng nhập thành công"));
        }

        /**
         * Đăng ký tài khoản mới
         * POST /api/v1/auth/register
         */
        @PostMapping("/register")
        public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
                if (request.getUsername() == null || request.getUsername().isBlank()) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error("Tên đăng nhập không được để trống"));
                }
                if (request.getPassword() == null || request.getPassword().length() < 6) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error("Mật khẩu phải có ít nhất 6 ký tự"));
                }
                if (userRepository.existsByUsername(request.getUsername().trim())) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(ApiResponse.error(
                                                        "Tên đăng nhập '" + request.getUsername() + "' đã tồn tại"));
                }

                User user = User.builder()
                                .name(request.getName() != null ? request.getName() : request.getUsername())
                                .username(request.getUsername().trim())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(request.getRole() != null ? request.getRole() : Role.EMPLOYEE)
                                .phone(request.getPhone())
                                .status(UserStatus.ACTIVE)
                                .baseSalary(0.0)
                                .build();

                User saved = userRepository.save(user);
                log.info("Đã đăng ký tài khoản mới: {} ({})", saved.getUsername(), saved.getRole());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.ok(UserResponse.fromEntity(saved), "Đăng ký tài khoản thành công"));
        }

        /**
         * Lấy thông tin người dùng hiện tại từ JWT trong Authorization header
         * GET /api/v1/auth/me
         * Header: Authorization: Bearer <token>
         */
        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserResponse>> getMe(
                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Không tìm thấy token xác thực"));
                }

                String token = authHeader.substring(7);
                try {
                        if (!jwtUtil.isTokenValid(token)) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(ApiResponse.error("Token không hợp lệ hoặc đã hết hạn"));
                        }

                        String username = jwtUtil.extractUsername(token);
                        return userRepository.findByUsername(username)
                                        .map(u -> ResponseEntity.ok(ApiResponse.ok(UserResponse.fromEntity(u), "OK")))
                                        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                        .body(ApiResponse.error("Tài khoản không tồn tại")));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(ApiResponse.error("Token không hợp lệ: " + e.getMessage()));
                }
        }

}
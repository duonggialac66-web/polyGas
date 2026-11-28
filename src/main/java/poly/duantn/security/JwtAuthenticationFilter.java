package poly.duantn.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import poly.duantn.repository.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter — chạy 1 lần mỗi request.
 * Đọc header "Authorization: Bearer <token>", xác thực JWT, set SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Không có header hoặc không phải Bearer → bỏ qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("JWT token không hợp lệ hoặc đã hết hạn: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            final String username = jwtUtil.extractUsername(token);
            final String rolesStr = jwtUtil.extractRoles(token);

            // Chỉ set SecurityContext nếu chưa được xác thực trong request này
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Nạp authorities từ claim "roles" trong token (khai báo final/effectively final để dùng trong lambda)
                final List<SimpleGrantedAuthority> authorities = (rolesStr != null && !rolesStr.isBlank())
                        ? Arrays.stream(rolesStr.split(","))
                                .map(String::trim)
                                .filter(r -> !r.isEmpty())
                                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                                .collect(Collectors.toList())
                        : List.of();

                // Nạp UserDetails để kiểm tra trạng thái tài khoản (INACTIVE / LOCKED)
                UserDetails userDetails = userRepository.findByUsername(username)
                        .map(user -> org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(authorities)
                                .accountExpired(false)
                                .accountLocked(user.getStatus() ==
                                        poly.duantn.entity.enums.UserStatus.INACTIVE)
                                .credentialsExpired(false)
                                .disabled(false)
                                .build())
                        .orElse(null);

                // Trong Spring Security UserDetails: isAccountNonLocked() trả về true nếu tài khoản KHÔNG bị khóa
                if (userDetails != null && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT auth OK: {} → {}", username, rolesStr);
                }
            }

        } catch (Exception e) {
            log.warn("Lỗi xử lý JWT filter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Bỏ qua filter cho các endpoint public (login, register)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }
}
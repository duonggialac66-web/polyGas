package poly.duantn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * Cấu hình Cross-Origin Resource Sharing (CORS) cho Mô hình Tách biệt Frontend – Backend.
 * Cho phép Single Page Application (React / Vite / Vue / Mobile) gọi RESTful API an toàn.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Cho phép gửi credentials (Cookies, Authorization Headers)
        config.setAllowCredentials(true);

        // Hỗ trợ tất cả Origin của Frontend SPA (Localhost 5173, 3000, Domain triển khai thực tế)
        config.setAllowedOriginPatterns(Collections.singletonList("*"));

        // Cho phép các HTTP Headers thông dụng
        config.setAllowedHeaders(Arrays.asList(
                "Origin", "Content-Type", "Accept", "Authorization",
                "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));

        // Cho phép đầy đủ các phương thức RESTful
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Cache pre-flight response trong 1 giờ để tối ưu tốc độ
        config.setMaxAge(3600L);

        // Áp dụng cho toàn bộ đường dẫn REST API
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}


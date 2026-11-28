package poly.duantn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller điều hướng SPA (Single Page Application Forwarding)
 * Cho phép người dùng truy cập trực tiếp các đường dẫn trình duyệt như /employment, /dashboard
 * mà không bị lỗi 404 Whitelabel Error của Spring Boot.
 */
@Controller
public class SpaForwardController {

    @GetMapping(value = {
            "/employment",
            "/employment/**",
            "/employees",
            "/employees/**",
            "/dashboard",
            "/inventory",
            "/debt-reports",
            "/salary",
            "/settings"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
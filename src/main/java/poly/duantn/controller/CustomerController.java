package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.CustomerRequest;
import poly.duantn.dto.CustomerResponse;
import poly.duantn.service.CustomerService;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller cho Quản lý Khách hàng & Tọa độ GPS (/api/v1/customers)
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Lấy toàn bộ danh bạ khách hàng
     * GET /api/v1/customers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        log.info("API GET /api/v1/customers - Lấy toàn bộ danh bạ khách hàng");
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.ok(customers, "Lấy danh bạ khách hàng thành công"));
    }

    /**
     * Tìm kiếm khách hàng siêu tốc O(1) qua mã băm không dấu
     * GET /api/v1/customers/search?keyword=...
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> searchCustomers(
            @RequestParam(required = false) String keyword
    ) {
        log.info("API GET /api/v1/customers/search - keyword: {}", keyword);
        List<CustomerResponse> results = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(ApiResponse.ok(results, "Tìm kiếm khách hàng thành công"));
    }

    /**
     * Thêm khách hàng mới, lưu tọa độ GPS để hỗ trợ bản đồ dẫn đường cho shipper
     * POST /api/v1/customers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerRequest request
    ) {
        log.info("API POST /api/v1/customers - Thêm khách hàng mới: {}", request != null ? request.getName() : null);
        CustomerResponse created = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "Thêm khách hàng mới thành công"));
    }

    /**
     * Chi tiết khách hàng theo ID
     * GET /api/v1/customers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable UUID id) {
        log.info("API GET /api/v1/customers/{}", id);
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.ok(customer, "Lấy thông tin khách hàng thành công"));
    }
}
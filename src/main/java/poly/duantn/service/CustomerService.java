package poly.duantn.service;

import poly.duantn.dto.CustomerRequest;
import poly.duantn.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    /**
     * Lấy danh bạ khách hàng
     */
    List<CustomerResponse> getAllCustomers();

    /**
     * Tìm kiếm khách hàng siêu tốc (tìm kiếm chính xác bằng nameHash hoặc tìm theo từ khóa chuẩn hóa)
     */
    List<CustomerResponse> searchCustomers(String keyword);

    /**
     * Thêm mới khách hàng với tọa độ GPS
     */
    CustomerResponse createCustomer(CustomerRequest request);

    /**
     * Lấy thông tin chi tiết một khách hàng theo ID
     */
    CustomerResponse getCustomerById(UUID id);
}

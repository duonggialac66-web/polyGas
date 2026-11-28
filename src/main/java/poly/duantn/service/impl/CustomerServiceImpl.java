package poly.duantn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.duantn.dto.CustomerRequest;
import poly.duantn.dto.CustomerResponse;
import poly.duantn.entity.Customer;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.CustomerRepository;
import poly.duantn.service.CustomerService;
import poly.duantn.util.NameHashUtil;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Cài đặt chi tiết nghiệp vụ Khách hàng & Tọa độ GPS (Customer Module)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        log.info("Lấy toàn bộ danh bạ khách hàng");
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }

        String searchHash = NameHashUtil.generateSearchHash(keyword);
        log.info("Tìm kiếm khách hàng bằng nameHash: {}", searchHash);

        List<Customer> customers = customerRepository.findByNameHashStartingWith(searchHash);
        if (customers.isEmpty()) {
            customers = customerRepository.findByNameContainingIgnoreCase(keyword.trim());
        }

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên khách hàng không được để trống");
        }

        String nameHash = NameHashUtil.generateSearchHash(request.getName());

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .nameHash(nameHash)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .address(request.getAddress() != null ? request.getAddress().trim() : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .notes(request.getNotes())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Thêm mới khách hàng thành công: ID={}, Name={}", saved.getId(), saved.getName());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng", "id", id));
        return mapToResponse(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .nameHash(customer.getNameHash())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .latitude(customer.getLatitude())
                .longitude(customer.getLongitude())
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

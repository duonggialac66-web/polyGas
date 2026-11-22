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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
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

        String trimmed = keyword.trim();
        String normalized = NameHashUtil.normalize(trimmed);

        log.info("Searching customers with keyword='{}', normalized='{}'", trimmed, normalized);

        // 1. Tìm kiếm siêu tốc qua chỉ mục name_hash O(1)
        if (!normalized.isEmpty()) {
            Optional<Customer> exactCustomer = customerRepository.findByNameHash(normalized);
            if (exactCustomer.isPresent()) {
                return Collections.singletonList(mapToResponse(exactCustomer.get()));
            }
        }

        // 2. Tìm kiếm theo tên có dấu hoặc SĐT
        List<Customer> byNameOrPhone = customerRepository.findByNameContainingIgnoreCase(trimmed);
        if (!byNameOrPhone.isEmpty()) {
            return byNameOrPhone.stream().map(this::mapToResponse).collect(Collectors.toList());
        }

        // 3. Quét kiểm tra nameHash chứa từ khóa đã chuẩn hóa hoặc SĐT/Địa chỉ
        List<Customer> allCustomers = customerRepository.findAll();
        List<CustomerResponse> result = new ArrayList<>();
        for (Customer c : allCustomers) {
            boolean matchesHash = (c.getNameHash() != null && !normalized.isEmpty() && c.getNameHash().contains(normalized));
            boolean matchesPhone = (c.getPhone() != null && c.getPhone().contains(trimmed));
            boolean matchesAddress = (c.getAddress() != null && c.getAddress().toLowerCase().contains(trimmed.toLowerCase()));

            if (matchesHash || matchesPhone || matchesAddress) {
                result.add(mapToResponse(c));
            }
        }

        return result;
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Tên khách hàng không được để trống");
        }

        String nameHash = NameHashUtil.generateNameHash(request.getName());
        if (customerRepository.existsByNameHash(nameHash)) {
            throw new BadRequestException("Khách hàng với tên tương tự đã tồn tại trong hệ thống (trùng lặp mã nhận diện: " + nameHash + ")");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .address(request.getAddress() != null ? request.getAddress().trim() : null)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .notes(request.getNotes())
                .nameHash(nameHash)
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Created new customer id={}, name='{}', nameHash='{}'", saved.getId(), saved.getName(), saved.getNameHash());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapToResponse(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .latitude(customer.getLatitude())
                .longitude(customer.getLongitude())
                .notes(customer.getNotes())
                .nameHash(customer.getNameHash())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

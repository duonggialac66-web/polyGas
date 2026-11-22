package poly.duantn.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.InventoryResponse;
import poly.duantn.dto.InventoryStockRequest;
import poly.duantn.entity.Inventory;
import poly.duantn.exception.BadRequestException;
import poly.duantn.exception.ResourceNotFoundException;
import poly.duantn.repository.InventoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RESTful Controller quản lý tồn kho bình gas và vỏ rỗng
 * Resource path: /api/v1/inventories
 */
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    /**
     * Lấy toàn bộ danh sách tồn kho bình gas kèm trạng thái cảnh báo thiếu hàng
     * GET /api/v1/inventories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventories() {
        log.info("Lấy danh sách tồn kho gas toàn hệ thống");
        List<Inventory> inventories = inventoryRepository.findAll();

        List<InventoryResponse> responses = inventories.stream()
                .map(inv -> InventoryResponse.builder()
                        .id(inv.getId())
                        .containerType(inv.getContainerType())
                        .fullQuantity(inv.getFullQuantity())
                        .emptyQuantity(inv.getEmptyQuantity())
                        .warningThreshold(inv.getWarningThreshold())
                        .isWarning(inv.getFullQuantity() <= inv.getWarningThreshold())
                        .updatedAt(inv.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(responses, "Lấy danh sách tồn kho thành công"));
    }

    /**
     * Cập nhật điều chỉnh tồn kho (Nhập/Xuất bình đầy & vỏ rỗng)
     * PUT /api/v1/inventories/{id}/stock
     */
    @PutMapping("/{id}/stock")
    @Transactional
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStock(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryStockRequest request
    ) {
        log.info("Điều chỉnh tồn kho ID: {}, SL: {}, Loại: {}", id, request.getQuantity(), request.getType());

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tồn kho bình gas", "id", id));

        int qty = request.getQuantity();
        String type = request.getType().toUpperCase().trim();

        switch (type) {
            case "IMPORT_FULL":
                inventory.setFullQuantity(inventory.getFullQuantity() + qty);
                break;

            case "EXPORT_FULL":
                if (inventory.getFullQuantity() < qty) {
                    throw new BadRequestException("Số lượng bình đầy trong kho (" + inventory.getFullQuantity() + ") không đủ để xuất!");
                }
                inventory.setFullQuantity(inventory.getFullQuantity() - qty);
                break;

            case "IMPORT_EMPTY":
                inventory.setEmptyQuantity(inventory.getEmptyQuantity() + qty);
                break;

            case "EXPORT_EMPTY":
                if (inventory.getEmptyQuantity() < qty) {
                    throw new BadRequestException("Số lượng vỏ rỗng trong kho (" + inventory.getEmptyQuantity() + ") không đủ để xuất!");
                }
                inventory.setEmptyQuantity(inventory.getEmptyQuantity() - qty);
                break;

            default:
                throw new BadRequestException("Loại thao tác kho không hợp lệ: " + type + ". Cho phép: IMPORT_FULL, EXPORT_FULL, IMPORT_EMPTY, EXPORT_EMPTY");
        }

        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inventory);

        InventoryResponse response = InventoryResponse.builder()
                .id(saved.getId())
                .containerType(saved.getContainerType())
                .fullQuantity(saved.getFullQuantity())
                .emptyQuantity(saved.getEmptyQuantity())
                .warningThreshold(saved.getWarningThreshold())
                .isWarning(saved.getFullQuantity() <= saved.getWarningThreshold())
                .updatedAt(saved.getUpdatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response, "Cập nhật tồn kho thành công"));
    }

    /**
     * Nhập kho linh hoạt (Tự động thêm loại bình mới nếu chưa có trong danh mục)
     * POST /api/v1/inventories/import-flexible
     */
    @PostMapping("/import-flexible")
    @Transactional
    public ResponseEntity<ApiResponse<InventoryResponse>> importFlexible(@RequestBody Map<String, Object> body) {
        String containerType = (String) body.get("containerType");
        if (containerType == null || containerType.trim().isEmpty()) {
            throw new BadRequestException("Tên loại bình gas không được để trống");
        }
        containerType = containerType.trim();
        int qty = body.get("quantity") != null ? Integer.parseInt(body.get("quantity").toString()) : 0;
        String type = body.get("type") != null ? body.get("type").toString().toUpperCase() : "IMPORT_FULL";
        int threshold = body.get("warningThreshold") != null ? Integer.parseInt(body.get("warningThreshold").toString()) : 10;

        String finalContainerType = containerType;
        Inventory inventory = inventoryRepository.findByContainerType(containerType)
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .containerType(finalContainerType)
                        .fullQuantity(0)
                        .emptyQuantity(0)
                        .warningThreshold(threshold)
                        .build()));

        if ("IMPORT_EMPTY".equalsIgnoreCase(type)) {
            inventory.setEmptyQuantity(inventory.getEmptyQuantity() + qty);
        } else {
            inventory.setFullQuantity(inventory.getFullQuantity() + qty);
        }
        if (threshold > 0) {
            inventory.setWarningThreshold(threshold);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inventory);

        InventoryResponse response = InventoryResponse.builder()
                .id(saved.getId())
                .containerType(saved.getContainerType())
                .fullQuantity(saved.getFullQuantity())
                .emptyQuantity(saved.getEmptyQuantity())
                .warningThreshold(saved.getWarningThreshold())
                .isWarning(saved.getFullQuantity() <= saved.getWarningThreshold())
                .updatedAt(saved.getUpdatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.ok(response, "Nhập kho linh hoạt thành công"));
    }
}

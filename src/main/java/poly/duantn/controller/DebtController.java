package poly.duantn.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.duantn.dto.ApiResponse;
import poly.duantn.dto.DebtResponse;
import poly.duantn.dto.PayDebtRequest;
import poly.duantn.service.DebtService;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller cho Sổ Nợ Khách Hàng (/api/v1/debts)
 * Cung cấp API quản lý các đơn hàng chưa thanh toán và thực hiện thu nợ.
 */
@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
@Slf4j
public class DebtController {

    private final DebtService debtService;

    /**
     * Lấy danh sách toàn bộ các đơn hàng đang bị nợ (payment_status = 'DEBT')
     * GET /api/v1/debts?customer=...&employeeId=...
     *
     * @param customer Từ khóa tìm kiếm theo tên hoặc SĐT khách hàng
     * @param employeeId ID nhân viên giao hàng phụ trách đơn
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DebtResponse>>> getDebts(
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) UUID employeeId
    ) {
        log.info("API GET /api/v1/debts - customer: {}, employeeId: {}", customer, employeeId);
        List<DebtResponse> debts = debtService.getDebts(customer, employeeId);
        return ResponseEntity.ok(ApiResponse.ok(debts, "Lấy danh sách sổ nợ thành công"));
    }

    /**
     * Thu tiền nợ từ khách hàng
     * POST /api/v1/debts/{reportId}/pay
     *
     * @param reportId ID phiếu giao gas đang nợ
     * @param request Body chứa số tiền khách trả thêm (amountPaid) và ghi chú nếu có
     */
    @PostMapping("/{reportId}/pay")
    public ResponseEntity<ApiResponse<DebtResponse>> payDebt(
            @PathVariable UUID reportId,
            @RequestBody PayDebtRequest request
    ) {
        log.info("API POST /api/v1/debts/{}/pay - amountPaid: {}", reportId, request != null ? request.getAmountPaid() : null);
        DebtResponse updatedDebt = debtService.payDebt(reportId, request);
        return ResponseEntity.ok(ApiResponse.ok(updatedDebt, "Thu tiền nợ thành công"));
    }
}

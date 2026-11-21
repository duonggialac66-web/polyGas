package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO yêu cầu thu nợ từ khách hàng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayDebtRequest {

    /**
     * Số tiền khách trả thêm trong đợt thu này
     */
    private Double amountPaid;

    /**
     * Ghi chú thu nợ (tùy chọn)
     */
    private String notes;
}

package poly.duantn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload trả về thông tin chi phí vận hành
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private LocalDate expenseDate;
    private String description;
    private Double amount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
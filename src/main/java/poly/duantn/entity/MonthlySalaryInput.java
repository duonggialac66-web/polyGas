package poly.duantn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "monthly_salary_inputs", uniqueConstraints = {
    @UniqueConstraint(name = "UK_Salary_Employee_Month", columnNames = {"employee_id", "month"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySalaryInput {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    /**
     * Khóa ngoại liên kết tới công thức SpEL được áp dụng để tính lương cho tháng này.
     * Đảm bảo tính minh bạch, lưu vết lịch sử (Audit Trail) khi công thức lương thay đổi theo năm.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_formula_id")
    private SalaryFormula salaryFormula;

    @Column(name = "month", nullable = false, length = 7)
    private String month;

    @Column(name = "work_days", nullable = false)
    @Builder.Default
    private Double workDays = 26.0;

    @Column(name = "overtime", nullable = false)
    @Builder.Default
    private Double overtime = 1.0;

    @Column(name = "bonus", nullable = false)
    @Builder.Default
    private Double bonus = 0.0;

    @Column(name = "calculated_salary", nullable = false)
    @Builder.Default
    private Double calculatedSalary = 0.0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.workDays == null) {
            this.workDays = 26.0;
        }
        if (this.overtime == null) {
            this.overtime = 1.0;
        }
        if (this.bonus == null) {
            this.bonus = 0.0;
        }
        if (this.calculatedSalary == null) {
            this.calculatedSalary = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


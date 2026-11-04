package poly.duantn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "salary_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "container_type", nullable = false, unique = true, length = 100)
    private String containerType;

    @Column(name = "commission_per_unit", nullable = false)
    @Builder.Default
    private Double commissionPerUnit = 0.0;

    @Column(name = "milestone_bonus_threshold")
    private Integer milestoneBonusThreshold;

    @Column(name = "milestone_bonus_amount")
    private Double milestoneBonusAmount;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.commissionPerUnit == null) {
            this.commissionPerUnit = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


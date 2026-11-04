package poly.duantn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "container_type", nullable = false, unique = true, length = 100)
    private String containerType;

    @Column(name = "full_quantity", nullable = false)
    @Builder.Default
    private Integer fullQuantity = 0;

    @Column(name = "empty_quantity", nullable = false)
    @Builder.Default
    private Integer emptyQuantity = 0;

    @Column(name = "warning_threshold", nullable = false)
    @Builder.Default
    private Integer warningThreshold = 10;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.fullQuantity == null) {
            this.fullQuantity = 0;
        }
        if (this.emptyQuantity == null) {
            this.emptyQuantity = 0;
        }
        if (this.warningThreshold == null) {
            this.warningThreshold = 10;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


package poly.duantn.entity;

import jakarta.persistence.*;
import lombok.*;
import poly.duantn.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delivery_reports", indexes = {
        @Index(name = "IX_DeliveryReports_Employee_Date", columnList = "employee_id, report_date"),
        @Index(name = "IX_DeliveryReports_PaymentStatus", columnList = "payment_status"),
        @Index(name = "IX_DeliveryReports_Inventory", columnList = "inventory_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Khóa ngoại liên kết tới nhân viên lập đơn giao gas
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    /**
     * Khóa ngoại liên kết tới khách hàng (cho phép null nếu là khách vãng lai)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    /**
     * Khóa ngoại chuẩn liên kết trực tiếp tới loại bình gas trong kho
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @Builder.Default
    private Double unitPrice = 0.0;

    @Column(name = "total", nullable = false)
    @Builder.Default
    private Double total = 0.0;

    @Column(name = "actual_received", nullable = false)
    @Builder.Default
    private Double actualReceived = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Column(name = "notes", columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.reportDate == null) {
            this.reportDate = LocalDate.now();
        }
        if (this.unitPrice == null) {
            this.unitPrice = 0.0;
        }
        if (this.total == null) {
            this.total = (this.quantity != null ? this.quantity : 0) * this.unitPrice;
        }
        if (this.actualReceived == null) {
            this.actualReceived = 0.0;
        }
        if (this.paymentStatus == null) {
            this.paymentStatus = (this.actualReceived >= this.total) ? PaymentStatus.PAID : PaymentStatus.DEBT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.unitPrice != null && this.quantity != null) {
            this.total = this.quantity * this.unitPrice;
        }
    }
}

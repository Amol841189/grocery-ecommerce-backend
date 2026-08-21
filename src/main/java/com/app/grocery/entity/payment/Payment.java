package com.app.grocery.entity.payment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.app.grocery.entity.order.Order;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @Column(
        name = "payment_id",
        nullable = false,
        unique = true,
        length = 40
    )
    private String paymentId;

    // =============================================
    // ORDER
    // =============================================

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "order_id",
        nullable = false,
        unique = true
    )
    private Order order;

    // =============================================
    // AMOUNT
    // =============================================

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    // =============================================
    // PAYMENT METHOD
    // =============================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    // =============================================
    // STATUS
    // =============================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    // =============================================
    // TRANSACTION ID
    // =============================================

    @Column(length = 100)
    private String transactionId;

    // =============================================
    // AUDIT
    // =============================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // =============================================
    // CREATE
    // =============================================

    @PrePersist
    protected void onCreate() {

        if (paymentId == null || paymentId.isBlank()) {
            paymentId = "PAY-" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (status == null) {
            status = PaymentStatus.CREATED;
        }

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // =============================================
    // UPDATE
    // =============================================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
package com.app.grocery.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.app.grocery.entity.user.User;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, unique = true, length = 20)
    private String orderId;

    // =====================================================
    // USER
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =====================================================
    // ORDER ITEMS
    // =====================================================

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // =====================================================
    // AMOUNTS
    // =====================================================

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal itemTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal handlingFee;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grandTotal;

    // =====================================================
    // STATUS
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    // =====================================================
    // AUDIT
    // =====================================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        if (orderId == null || orderId.isBlank()) {
            orderId = "ORD-" +
                    java.util.UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (status == null) {
            status = OrderStatus.PAYMENT_PENDING;
        }

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // =====================================================
    // PRE UPDATE
    // =====================================================

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
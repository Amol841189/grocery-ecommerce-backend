package com.app.grocery.dto.payment.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.grocery.entity.payment.PaymentMethod;
import com.app.grocery.entity.payment.PaymentStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String paymentId;

    private String orderId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
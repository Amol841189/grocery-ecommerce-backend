package com.app.grocery.dto;

import com.app.grocery.entity.PaymentMethod;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    private String orderId;

    private PaymentMethod paymentMethod;
}
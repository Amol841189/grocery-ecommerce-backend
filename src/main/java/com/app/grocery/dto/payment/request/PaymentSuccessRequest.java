package com.app.grocery.dto.payment.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessRequest {

    private String transactionId;
}
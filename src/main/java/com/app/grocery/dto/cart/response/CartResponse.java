package com.app.grocery.dto.cart.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private String cartId;

    private List<CartItemResponse> items;

    private Integer totalItems;

    private BigDecimal itemTotal;

    private BigDecimal deliveryFee;

    private BigDecimal handlingFee;

    private BigDecimal grandTotal;
}
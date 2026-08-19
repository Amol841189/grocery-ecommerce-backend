package com.app.grocery.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private String productId;

    private String productName;

    private String imageUrl;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String unit;

    private Integer quantity;

    private BigDecimal itemTotal;
}
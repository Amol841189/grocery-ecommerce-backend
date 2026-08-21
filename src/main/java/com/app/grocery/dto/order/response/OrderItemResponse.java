package com.app.grocery.dto.order.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long orderItemId;

    private String productId;

    private String productName;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer quantity;

    private BigDecimal itemTotal;
}
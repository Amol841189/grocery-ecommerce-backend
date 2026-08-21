package com.app.grocery.dto.order.response;

import com.app.grocery.dto.order.response.OrderItemResponse;
import com.app.grocery.entity.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String orderId;

    private String userId;

    private List<OrderItemResponse> items;

    private Integer totalItems;

    private BigDecimal itemTotal;

    private BigDecimal deliveryFee;

    private BigDecimal handlingFee;

    private BigDecimal grandTotal;

    private OrderStatus status;

    private LocalDateTime createdAt;
}
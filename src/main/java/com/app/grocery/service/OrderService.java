package com.app.grocery.service;

import com.app.grocery.dto.CreateOrderRequest;
import com.app.grocery.dto.OrderItemResponse;
import com.app.grocery.dto.OrderResponse;
import com.app.grocery.entity.Cart;
import com.app.grocery.entity.CartItem;
import com.app.grocery.entity.Order;
import com.app.grocery.entity.OrderItem;
import com.app.grocery.entity.Product;
import com.app.grocery.entity.User;
import com.app.grocery.exception.InsufficientStockException;
import com.app.grocery.exception.ResourceNotFoundException;
import com.app.grocery.repository.CartRepository;
import com.app.grocery.repository.OrderRepository;
import com.app.grocery.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final UserRepository userRepository;
        private final CartRepository cartRepository;
        private final OrderRepository orderRepository;

        // =====================================================
        // CREATE ORDER
        // =====================================================

        @Transactional
        public OrderResponse createOrder(CreateOrderRequest request) {

                // ---------------------------------------------
                // USER
                // ---------------------------------------------

                User user = userRepository
                                .findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found: " + request.getUserId()));

                // ---------------------------------------------
                // CART
                // ---------------------------------------------

                Cart cart = cartRepository
                                .findByUser_UserId(user.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cart not found for user: "
                                                                + user.getUserId()));

                // ---------------------------------------------
                // CHECK CART
                // ---------------------------------------------

                if (cart.getItems() == null || cart.getItems().isEmpty()) {
                        throw new IllegalStateException(
                                        "Cannot create order from empty cart");
                }

                // ---------------------------------------------
                // CALCULATE ITEM TOTAL
                // ---------------------------------------------

                BigDecimal itemTotal = BigDecimal.ZERO;

                for (CartItem cartItem : cart.getItems()) {

                        Product product = cartItem.getProduct();

                        BigDecimal price = getEffectivePrice(product);

                        BigDecimal subtotal = price.multiply(
                                        BigDecimal.valueOf(cartItem.getQuantity()));

                        itemTotal = itemTotal.add(subtotal);
                }

                // ---------------------------------------------
                // FEES
                // ---------------------------------------------

                BigDecimal deliveryFee = BigDecimal.ZERO;

                BigDecimal handlingFee = BigDecimal.ZERO;

                // ---------------------------------------------
                // GRAND TOTAL
                // ---------------------------------------------

                BigDecimal grandTotal = itemTotal
                                .add(deliveryFee)
                                .add(handlingFee);

                // ---------------------------------------------
                // CREATE ORDER
                // ---------------------------------------------

                Order order = Order.builder()
                                .user(user)
                                .itemTotal(itemTotal)
                                .deliveryFee(deliveryFee)
                                .handlingFee(handlingFee)
                                .grandTotal(grandTotal)
                                .build();

                // ---------------------------------------------
                // CREATE ORDER ITEMS + RESERVE INVENTORY
                // ---------------------------------------------

                for (CartItem cartItem : cart.getItems()) {

                        Product product = cartItem.getProduct();

                        // -----------------------------------------
                        // INVENTORY CHECK
                        // -----------------------------------------

                        if (product.getInventory() == null) {
                                throw new ResourceNotFoundException(
                                                "Product inventory not available: "
                                                                + product.getProductId());
                        }

                        // -----------------------------------------
                        // AVAILABLE STOCK
                        // -----------------------------------------

                        int currentQty = product.getInventory().getQuantity();
                        int reservedQty = product.getInventory().getReservedQuantity();

                        if (currentQty < 0 || reservedQty < 0) {
                                throw new IllegalStateException(
                                                "Invalid inventory for product: "
                                                                + product.getProductId());
                        }

                        int availableQuantity = currentQty - reservedQty;

                        if (availableQuantity < 0) {
                                throw new IllegalStateException(
                                                "Invalid inventory reservation for product: "
                                                                + product.getProductId());
                        }

                        if (availableQuantity == 0) {
                                throw new InsufficientStockException(
                                                "Product is out of stock: "
                                                                + product.getProductId());
                        }
                        int requestedQuantity = cartItem.getQuantity();

                        // -----------------------------------------
                        // STOCK CHECK
                        // -----------------------------------------

                        if (requestedQuantity > availableQuantity) {

                                throw new InsufficientStockException(
                                                "Only "
                                                                + availableQuantity
                                                                + " items available for product: "
                                                                + product.getProductId());
                        }

                        // -----------------------------------------
                        // RESERVE INVENTORY
                        // -----------------------------------------

                        product.getInventory().setReservedQuantity(
                                        product.getInventory().getReservedQuantity()
                                                        + requestedQuantity);

                        // -----------------------------------------
                        // PRICE
                        // -----------------------------------------

                        BigDecimal price = getEffectivePrice(product);

                        BigDecimal subtotal = price.multiply(
                                        BigDecimal.valueOf(requestedQuantity));

                        // -----------------------------------------
                        // CREATE ORDER ITEM
                        // -----------------------------------------

                        OrderItem orderItem = OrderItem.builder()
                                        .order(order)
                                        .product(product)
                                        .quantity(requestedQuantity)
                                        .price(price)
                                        .subtotal(subtotal)
                                        .build();

                        order.getItems().add(orderItem);
                }

                // ---------------------------------------------
                // SAVE ORDER
                // ---------------------------------------------

                Order savedOrder = orderRepository.save(order);

                // ---------------------------------------------
                // BUILD RESPONSE
                // ---------------------------------------------

                return buildOrderResponse(savedOrder);
        }

        // =====================================================
        // EFFECTIVE PRICE
        // =====================================================

        private BigDecimal getEffectivePrice(Product product) {
                if (product.getDiscountPrice() != null &&
                                product.getDiscountPrice().compareTo(product.getPrice()) < 0) {
                        return product.getDiscountPrice();
                }

                return product.getPrice();
        }

        // =====================================================
        // BUILD RESPONSE
        // =====================================================

        private OrderResponse buildOrderResponse(Order order) {
                List<OrderItemResponse> items = order
                                .getItems()
                                .stream()
                                .map(item -> OrderItemResponse
                                                .builder()
                                                .orderItemId(item.getOrderItemId())
                                                .productId(item.getProduct().getProductId())
                                                .productName(item.getProduct().getName())
                                                .price(item.getProduct().getPrice())
                                                .discountPrice(item.getProduct().getDiscountPrice())
                                                .quantity(item.getQuantity())
                                                .itemTotal(item.getSubtotal())
                                                .build())
                                .toList();

                int totalItems = order
                                .getItems()
                                .stream()
                                .mapToInt(OrderItem::getQuantity)
                                .sum();

                return OrderResponse
                                .builder()
                                .orderId(order.getOrderId())
                                .userId(order.getUser().getUserId())
                                .items(items)
                                .totalItems(totalItems)
                                .itemTotal(order.getItemTotal())
                                .deliveryFee(order.getDeliveryFee())
                                .handlingFee(order.getHandlingFee())
                                .grandTotal(order.getGrandTotal())
                                .status(order.getStatus())
                                .createdAt(order.getCreatedAt())
                                .build();
        }
}

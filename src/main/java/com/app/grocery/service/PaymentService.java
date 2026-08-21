package com.app.grocery.service;

import com.app.grocery.dto.payment.request.CreatePaymentRequest;
import com.app.grocery.dto.payment.request.PaymentFailedRequest;
import com.app.grocery.dto.payment.request.PaymentSuccessRequest;
import com.app.grocery.dto.payment.response.PaymentResponse;
import com.app.grocery.entity.Inventory;
import com.app.grocery.entity.Order;
import com.app.grocery.entity.OrderItem;
import com.app.grocery.entity.OrderStatus;
import com.app.grocery.entity.Payment;
import com.app.grocery.entity.PaymentStatus;
import com.app.grocery.entity.Product;
import com.app.grocery.exception.ResourceNotFoundException;
import com.app.grocery.repository.OrderRepository;
import com.app.grocery.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;

        // =====================================================
        // CREATE PAYMENT
        // =====================================================

        @Transactional
        public PaymentResponse createPayment(CreatePaymentRequest request) {

                // ---------------------------------------------
                // ORDER
                // ---------------------------------------------

                Order order = orderRepository
                                .findById(request.getOrderId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order not found: "
                                                                + request.getOrderId()));

                // ---------------------------------------------
                // CHECK ORDER STATUS
                // ---------------------------------------------

                if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {

                        throw new IllegalStateException(
                                        "Payment cannot be created for order: " + order.getOrderId());
                }

                // ---------------------------------------------
                // CHECK EXISTING PAYMENT
                // ---------------------------------------------

                if (paymentRepository
                                .existsByOrder_OrderId(order.getOrderId())) {

                        throw new IllegalStateException(
                                        "Payment already exists for order: " + order.getOrderId());
                }

                // ---------------------------------------------
                // CREATE PAYMENT
                // ---------------------------------------------

                Payment payment = Payment.builder()
                                .order(order)
                                .amount(order.getGrandTotal())
                                .paymentMethod(request.getPaymentMethod())
                                .status(PaymentStatus.CREATED)
                                .build();

                Payment savedPayment = paymentRepository.save(payment);

                return buildPaymentResponse(savedPayment);
        }

        @Transactional
        public PaymentResponse markPaymentSuccess(
                        String paymentId,
                        PaymentSuccessRequest request) {
                // ---------------------------------------------
                // PAYMENT
                // ---------------------------------------------

                Payment payment = paymentRepository
                                .findById(paymentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Payment not found: " + paymentId));

                // ---------------------------------------------
                // CHECK PAYMENT STATUS
                // ---------------------------------------------

                if (payment.getStatus() != PaymentStatus.CREATED) {
                        throw new IllegalStateException(
                                        "Payment cannot be completed. Current status: "
                                                        + payment.getStatus());
                }

                // ---------------------------------------------
                // ORDER
                // ---------------------------------------------

                Order order = payment.getOrder();

                if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
                        throw new IllegalStateException(
                                        "Order is not waiting for payment: "
                                                        + order.getOrderId());
                }

                // ---------------------------------------------
                // FINALIZE INVENTORY
                // ---------------------------------------------

                for (OrderItem orderItem : order.getItems()) {

                        Product product = orderItem.getProduct();

                        if (product.getInventory() == null) {
                                throw new ResourceNotFoundException(
                                                "Inventory not found for product: "
                                                                + product.getProductId());
                        }

                        Inventory inventory = product.getInventory();

                        int quantity = orderItem.getQuantity();

                        // Reduce actual stock
                        inventory.setQuantity(
                                        inventory.getQuantity() - quantity);

                        // Remove reservation
                        inventory.setReservedQuantity(
                                        inventory.getReservedQuantity() - quantity);
                }

                // ---------------------------------------------
                // PAYMENT SUCCESS
                // ---------------------------------------------

                payment.setStatus(PaymentStatus.SUCCESS);

                // ---------------------------------------------
                // ORDER CONFIRMED
                // ---------------------------------------------

                order.setStatus(OrderStatus.CONFIRMED);

                // ---------------------------------------------
                // SAVE
                // ---------------------------------------------

                paymentRepository.save(payment);
                orderRepository.save(order);

                // ---------------------------------------------
                // RESPONSE
                // ---------------------------------------------

                return buildPaymentResponse(payment);
        }

        @Transactional
        public PaymentResponse markPaymentFailed(
                        String paymentId,
                        PaymentFailedRequest request) {

                Payment payment = paymentRepository
                                .findById(paymentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Payment not found: " + paymentId));

                if (payment.getStatus() != PaymentStatus.CREATED) {
                        throw new IllegalStateException(
                                        "Payment cannot be failed. Current status: "
                                                        + payment.getStatus());
                }

                Order order = payment.getOrder();

                if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
                        throw new IllegalStateException(
                                        "Order is not waiting for payment: "
                                                        + order.getOrderId());
                }

                // ---------------------------------------------
                // SAVE TRANSACTION ID
                // ---------------------------------------------

                if (request != null) {
                        payment.setTransactionId(
                                        request.getTransactionId());
                }

                // ---------------------------------------------
                // RELEASE RESERVED INVENTORY
                // ---------------------------------------------

                for (OrderItem orderItem : order.getItems()) {

                        Product product = orderItem.getProduct();

                        Inventory inventory = product.getInventory();

                        int reservedQuantity = inventory.getReservedQuantity();

                        int orderedQuantity = orderItem.getQuantity();

                        if (reservedQuantity < orderedQuantity) {
                                throw new IllegalStateException(
                                                "Reserved inventory is insufficient for product: "
                                                                + product.getProductId());
                        }

                        inventory.setReservedQuantity(
                                        reservedQuantity - orderedQuantity);
                }

                // ---------------------------------------------
                // PAYMENT FAILED
                // ---------------------------------------------

                payment.setStatus(PaymentStatus.FAILED);

                // ---------------------------------------------
                // ORDER FAILED
                // ---------------------------------------------

                order.setStatus(OrderStatus.PAYMENT_FAILED);

                paymentRepository.save(payment);
                orderRepository.save(order);

                return buildPaymentResponse(payment);
        }

        private PaymentResponse buildPaymentResponse(Payment payment) {

                return PaymentResponse.builder()
                                .paymentId(payment.getPaymentId())
                                .orderId(payment.getOrder().getOrderId())
                                .amount(payment.getAmount())
                                .paymentMethod(payment.getPaymentMethod())
                                .status(payment.getStatus())
                                .transactionId(payment.getTransactionId())
                                .createdAt(payment.getCreatedAt())
                                .updatedAt(payment.getUpdatedAt())
                                .build();
        }
}
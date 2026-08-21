package com.app.grocery.controller;

import com.app.grocery.dto.payment.request.CreatePaymentRequest;
import com.app.grocery.dto.payment.request.PaymentFailedRequest;
import com.app.grocery.dto.payment.request.PaymentSuccessRequest;
import com.app.grocery.dto.payment.response.PaymentResponse;
import com.app.grocery.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

        private final PaymentService paymentService;

        // =====================================================
        // CREATE PAYMENT
        // =====================================================

        @PostMapping
        public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {

                PaymentResponse response = paymentService.createPayment((request));
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/{paymentId}/success")
        public ResponseEntity<PaymentResponse> markPaymentSuccess(@PathVariable String paymentId,
                        @RequestBody PaymentSuccessRequest request) {

                return ResponseEntity.ok(paymentService.markPaymentSuccess(paymentId, request));
        }

        @PostMapping("/{paymentId}/failed")
        public ResponseEntity<PaymentResponse> markPaymentFailed(@PathVariable String paymentId,
                        @RequestBody PaymentFailedRequest request) {

                return ResponseEntity.ok(paymentService.markPaymentFailed(paymentId, request));
        }
}
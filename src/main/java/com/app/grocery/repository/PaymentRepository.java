package com.app.grocery.repository;

import com.app.grocery.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrder_OrderId(String orderId);

    boolean existsByOrder_OrderId(String orderId);
}
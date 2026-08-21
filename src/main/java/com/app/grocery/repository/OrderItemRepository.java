package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.order.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
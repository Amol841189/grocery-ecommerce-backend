package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.order.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUser_UserId(String userId);
}
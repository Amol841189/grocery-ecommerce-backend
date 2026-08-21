package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.cart.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

        Optional<Cart> findByUser_UserId(String userId);

}
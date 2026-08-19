package com.app.grocery.repository;

import com.app.grocery.entity.Cart;
import com.app.grocery.entity.CartItem;
import com.app.grocery.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(
            Cart cart,
            Product product
    );
}
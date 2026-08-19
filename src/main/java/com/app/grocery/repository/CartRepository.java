package com.app.grocery.repository;

import com.app.grocery.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository
        extends JpaRepository<Cart, String> {
}
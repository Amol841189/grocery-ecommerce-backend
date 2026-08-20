package com.app.grocery.repository;

import com.app.grocery.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

        Optional<Cart> findByUser_UserId(String userId);

}
package com.app.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.grocery.entity.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
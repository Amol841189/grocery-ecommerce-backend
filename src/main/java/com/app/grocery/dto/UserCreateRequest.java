package com.app.grocery.dto;

public record UserCreateRequest(
        String name,
        String email,
        String mobileNumber,
        String password
) {
}
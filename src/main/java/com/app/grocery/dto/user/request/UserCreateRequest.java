package com.app.grocery.dto.user.request;

public record UserCreateRequest(
        String name,
        String email,
        String mobileNumber,
        String password
) {
}
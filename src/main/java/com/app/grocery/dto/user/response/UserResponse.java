package com.app.grocery.dto.user.response;

import com.app.grocery.entity.Role;

public record UserResponse(
        String userId,
        String name,
        String email,
        String mobileNumber,
        Role role
) {
}
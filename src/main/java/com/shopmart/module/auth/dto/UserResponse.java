package com.shopmart.module.auth.dto;

import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String avatarUrl,
        boolean emailVerified,
        Set<String> roles
) {}

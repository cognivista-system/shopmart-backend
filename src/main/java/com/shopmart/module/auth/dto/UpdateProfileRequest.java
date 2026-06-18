package com.shopmart.module.auth.dto;

public record UpdateProfileRequest(
        String name,
        String phone,
        String avatarUrl
) {}

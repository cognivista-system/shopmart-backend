package com.shopmart.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank @Email(message = "Valid email is required") String email,
        String phone,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}

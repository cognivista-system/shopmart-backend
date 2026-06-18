package com.shopmart.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
        @NotBlank @Email String email,
        @NotBlank String code
) {}

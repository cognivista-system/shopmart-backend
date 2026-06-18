package com.shopmart.module.mobile.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceRegisterRequest(
        @NotBlank String token,
        String platform,
        String appVersion
) {}

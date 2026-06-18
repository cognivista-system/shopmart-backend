package com.shopmart.module.vendor.dto;

import jakarta.validation.constraints.NotBlank;

public record VendorRegistrationRequest(
        @NotBlank String storeName,
        String description,
        String logoUrl,
        String contactEmail,
        String contactPhone
) {}

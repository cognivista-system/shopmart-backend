package com.shopmart.module.brand.dto;

import jakarta.validation.constraints.NotBlank;

public record BrandRequest(
        @NotBlank String name,
        String description,
        String logoUrl,
        String bannerUrl,
        String metaTitle,
        String metaDescription,
        Boolean active
) {}

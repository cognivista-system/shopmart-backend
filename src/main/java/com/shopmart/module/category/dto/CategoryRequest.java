package com.shopmart.module.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        String description,
        String bannerUrl,
        String metaTitle,
        String metaDescription,
        Boolean active
) {}

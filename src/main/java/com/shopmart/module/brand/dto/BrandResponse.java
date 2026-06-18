package com.shopmart.module.brand.dto;

public record BrandResponse(
        Long id,
        String name,
        String slug,
        String description,
        String logoUrl,
        String bannerUrl,
        String metaTitle,
        String metaDescription,
        boolean active
) {}

package com.shopmart.module.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description,
        String bannerUrl,
        String metaTitle,
        String metaDescription,
        boolean active
) {}

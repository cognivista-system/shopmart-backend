package com.shopmart.module.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        String description,
        String sku,
        @NotNull @PositiveOrZero BigDecimal price,
        @PositiveOrZero BigDecimal salePrice,
        @PositiveOrZero Integer stock,
        Long categoryId,
        Long brandId,
        String status,
        Boolean featured,
        String metaTitle,
        String metaDescription,
        List<ProductImageDto> images,
        List<ProductVariantDto> variants
) {}

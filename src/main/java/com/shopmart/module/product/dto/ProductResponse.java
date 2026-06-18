package com.shopmart.module.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        String sku,
        BigDecimal price,
        BigDecimal salePrice,
        int stock,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName,
        String status,
        boolean featured,
        BigDecimal ratingAverage,
        int ratingCount,
        String metaTitle,
        String metaDescription,
        List<ProductImageDto> images,
        List<ProductVariantDto> variants,
        String rejectionReason
) {}

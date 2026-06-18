package com.shopmart.module.product.dto;

import java.math.BigDecimal;

public record ProductSummary(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal salePrice,
        String thumbnail,
        String brandName,
        BigDecimal ratingAverage,
        boolean featured
) {}

package com.shopmart.module.product.dto;

import java.math.BigDecimal;

public record ProductFilter(
        String q,
        Long categoryId,
        Long brandId,
        Boolean featured,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}

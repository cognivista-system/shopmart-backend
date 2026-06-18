package com.shopmart.module.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        Long variantId,
        String name,
        String thumbnail,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}

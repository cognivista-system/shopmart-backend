package com.shopmart.module.wishlist.dto;

import java.math.BigDecimal;

public record WishlistItemResponse(
        Long id,
        Long productId,
        String name,
        String slug,
        String thumbnail,
        BigDecimal price,
        BigDecimal salePrice,
        boolean inStock
) {}

package com.shopmart.module.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull Long productId,
        Long variantId,
        @NotNull @Min(1) Integer quantity
) {}

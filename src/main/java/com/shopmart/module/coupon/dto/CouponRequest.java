package com.shopmart.module.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponRequest(
        @NotBlank String code,
        String description,
        @NotBlank String discountType,            // PERCENTAGE | FIXED
        @NotNull @Positive BigDecimal discountValue,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        Integer usageLimit,
        Integer perUserLimit,
        Instant startsAt,
        Instant expiresAt,
        Boolean active
) {}

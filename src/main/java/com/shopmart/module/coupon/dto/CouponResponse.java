package com.shopmart.module.coupon.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        String description,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        Integer usageLimit,
        int usedCount,
        Integer perUserLimit,
        Instant startsAt,
        Instant expiresAt,
        boolean active
) {}

package com.shopmart.module.coupon.dto;

import java.math.BigDecimal;

/**
 * Result of validating a coupon against an order subtotal. Carries the coupon id
 * so the order flow can record a redemption once the order is persisted.
 */
public record CouponApplication(
        Long couponId,
        String code,
        BigDecimal discount
) {}

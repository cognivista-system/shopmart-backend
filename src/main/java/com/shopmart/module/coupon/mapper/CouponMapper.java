package com.shopmart.module.coupon.mapper;

import com.shopmart.module.coupon.dto.CouponResponse;
import com.shopmart.module.coupon.entity.Coupon;

public final class CouponMapper {
    private CouponMapper() {}

    public static CouponResponse toResponse(Coupon c) {
        return new CouponResponse(
                c.getId(), c.getCode(), c.getDescription(), c.getDiscountType().name(),
                c.getDiscountValue(), c.getMinOrderAmount(), c.getMaxDiscountAmount(),
                c.getUsageLimit(), c.getUsedCount(), c.getPerUserLimit(),
                c.getStartsAt(), c.getExpiresAt(), c.isActive());
    }
}

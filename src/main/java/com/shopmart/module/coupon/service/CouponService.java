package com.shopmart.module.coupon.service;

import com.shopmart.module.coupon.dto.CouponApplication;
import com.shopmart.module.coupon.dto.CouponRequest;
import com.shopmart.module.coupon.dto.CouponResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    CouponResponse create(CouponRequest request);
    CouponResponse update(Long id, CouponRequest request);
    void delete(Long id);
    CouponResponse getById(Long id);
    List<CouponResponse> getAll();

    /** Validates a coupon for a user + subtotal and returns the computed discount. Throws if invalid. */
    CouponApplication validate(String code, Long userId, BigDecimal subtotal);

    /** Records a successful redemption (called after the order is saved). */
    void markRedeemed(Long couponId, Long userId, Long orderId, BigDecimal discount);
}

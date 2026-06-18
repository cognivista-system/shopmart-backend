package com.shopmart.module.coupon.repository;

import com.shopmart.module.coupon.entity.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    long countByCouponIdAndUserId(Long couponId, Long userId);
    long countByCouponId(Long couponId);
}

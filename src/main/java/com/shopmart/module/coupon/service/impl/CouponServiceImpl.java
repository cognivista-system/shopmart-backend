package com.shopmart.module.coupon.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.coupon.dto.CouponApplication;
import com.shopmart.module.coupon.dto.CouponRequest;
import com.shopmart.module.coupon.dto.CouponResponse;
import com.shopmart.module.coupon.entity.Coupon;
import com.shopmart.module.coupon.entity.CouponRedemption;
import com.shopmart.module.coupon.entity.DiscountType;
import com.shopmart.module.coupon.mapper.CouponMapper;
import com.shopmart.module.coupon.repository.CouponRedemptionRepository;
import com.shopmart.module.coupon.repository.CouponRepository;
import com.shopmart.module.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;

    @Override
    @Transactional
    public CouponResponse create(CouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.code())) {
            throw new ConflictException("A coupon with this code already exists");
        }
        Coupon c = new Coupon();
        apply(c, request);
        return CouponMapper.toResponse(couponRepository.save(c));
    }

    @Override
    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon c = find(id);
        if (!c.getCode().equalsIgnoreCase(request.code())
                && couponRepository.existsByCodeIgnoreCase(request.code())) {
            throw new ConflictException("A coupon with this code already exists");
        }
        apply(c, request);
        return CouponMapper.toResponse(couponRepository.save(c));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        couponRepository.delete(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getById(Long id) {
        return CouponMapper.toResponse(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(CouponMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponApplication validate(String code, Long userId, BigDecimal subtotal) {
        Coupon c = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));

        Instant now = Instant.now();
        if (!c.isActive()) {
            throw new BadRequestException("This coupon is no longer active");
        }
        if (c.getStartsAt() != null && now.isBefore(c.getStartsAt())) {
            throw new BadRequestException("This coupon is not yet valid");
        }
        if (c.getExpiresAt() != null && now.isAfter(c.getExpiresAt())) {
            throw new BadRequestException("This coupon has expired");
        }
        if (c.getMinOrderAmount() != null && subtotal.compareTo(c.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Order total must be at least " + c.getMinOrderAmount()
                    + " to use this coupon");
        }
        if (c.getUsageLimit() != null && redemptionRepository.countByCouponId(c.getId()) >= c.getUsageLimit()) {
            throw new BadRequestException("This coupon has reached its usage limit");
        }
        if (c.getPerUserLimit() != null
                && redemptionRepository.countByCouponIdAndUserId(c.getId(), userId) >= c.getPerUserLimit()) {
            throw new BadRequestException("You have already used this coupon the maximum number of times");
        }

        BigDecimal discount = computeDiscount(c, subtotal);
        return new CouponApplication(c.getId(), c.getCode(), discount);
    }

    @Override
    @Transactional
    public void markRedeemed(Long couponId, Long userId, Long orderId, BigDecimal discount) {
        Coupon c = find(couponId);
        c.setUsedCount(c.getUsedCount() + 1);
        couponRepository.save(c);

        CouponRedemption r = new CouponRedemption();
        r.setCouponId(couponId);
        r.setUserId(userId);
        r.setOrderId(orderId);
        r.setDiscountApplied(discount);
        redemptionRepository.save(r);
    }

    // ---- helpers ----

    private BigDecimal computeDiscount(Coupon c, BigDecimal subtotal) {
        BigDecimal discount;
        if (c.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(c.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (c.getMaxDiscountAmount() != null && discount.compareTo(c.getMaxDiscountAmount()) > 0) {
                discount = c.getMaxDiscountAmount();
            }
        } else {
            discount = c.getDiscountValue();
        }
        // Never discount more than the subtotal
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private Coupon find(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
    }

    private void apply(Coupon c, CouponRequest r) {
        c.setCode(r.code().toUpperCase());
        c.setDescription(r.description());
        c.setDiscountType(parseType(r.discountType()));
        c.setDiscountValue(r.discountValue());
        c.setMinOrderAmount(r.minOrderAmount());
        c.setMaxDiscountAmount(r.maxDiscountAmount());
        c.setUsageLimit(r.usageLimit());
        c.setPerUserLimit(r.perUserLimit());
        c.setStartsAt(r.startsAt());
        c.setExpiresAt(r.expiresAt());
        c.setActive(r.active() == null || r.active());
    }

    private DiscountType parseType(String type) {
        try {
            return DiscountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("discountType must be PERCENTAGE or FIXED");
        }
    }
}

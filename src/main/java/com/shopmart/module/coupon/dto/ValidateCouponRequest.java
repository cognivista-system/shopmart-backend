package com.shopmart.module.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ValidateCouponRequest(
        @NotBlank String code,
        @NotNull BigDecimal orderAmount
) {}

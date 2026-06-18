package com.shopmart.module.vendor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record PayoutRequest(
        @NotNull @Positive BigDecimal amount,
        Instant periodStart,
        Instant periodEnd,
        String note
) {}

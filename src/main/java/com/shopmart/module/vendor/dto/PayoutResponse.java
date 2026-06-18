package com.shopmart.module.vendor.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PayoutResponse(
        Long id,
        Long vendorId,
        BigDecimal amount,
        String status,
        Instant periodStart,
        Instant periodEnd,
        Instant paidAt,
        String note,
        Instant createdAt
) {}

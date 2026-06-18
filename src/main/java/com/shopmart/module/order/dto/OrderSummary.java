package com.shopmart.module.order.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummary(
        Long id,
        String orderNumber,
        String status,
        BigDecimal total,
        int itemCount,
        Instant placedAt
) {}

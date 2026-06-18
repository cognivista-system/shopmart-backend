package com.shopmart.module.returns.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReturnResponse(
        Long id,
        Long orderId,
        Long userId,
        String reason,
        String status,
        BigDecimal refundAmount,
        String adminNote,
        String gatewayRefundId,
        Instant processedAt,
        Instant createdAt
) {}

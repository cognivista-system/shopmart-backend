package com.shopmart.module.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        String method,
        String provider,
        BigDecimal amount,
        String status,
        String gatewayRef,
        String transactionId,
        Instant createdAt
) {}

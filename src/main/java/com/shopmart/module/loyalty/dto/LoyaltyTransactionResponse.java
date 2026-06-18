package com.shopmart.module.loyalty.dto;

import java.time.Instant;

public record LoyaltyTransactionResponse(
        Long id, int points, String type, Long orderId, String description, Instant createdAt) {}

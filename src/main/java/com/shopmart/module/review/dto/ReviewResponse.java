package com.shopmart.module.review.dto;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long productId,
        String userName,
        int rating,
        String title,
        String comment,
        boolean verifiedPurchase,
        String status,
        Instant createdAt
) {}

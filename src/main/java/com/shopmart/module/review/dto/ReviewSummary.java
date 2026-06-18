package com.shopmart.module.review.dto;

public record ReviewSummary(
        Long productId,
        double averageRating,
        long totalReviews
) {}

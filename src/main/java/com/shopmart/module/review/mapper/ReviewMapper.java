package com.shopmart.module.review.mapper;

import com.shopmart.module.review.dto.ReviewResponse;
import com.shopmart.module.review.entity.Review;

public final class ReviewMapper {
    private ReviewMapper() {}

    public static ReviewResponse toResponse(Review r, String userName) {
        return new ReviewResponse(r.getId(), r.getProductId(), userName, r.getRating(),
                r.getTitle(), r.getComment(), r.isVerifiedPurchase(), r.getStatus().name(), r.getCreatedAt());
    }
}

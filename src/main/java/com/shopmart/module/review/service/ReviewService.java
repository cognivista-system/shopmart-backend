package com.shopmart.module.review.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.review.dto.ReviewRequest;
import com.shopmart.module.review.dto.ReviewResponse;
import com.shopmart.module.review.dto.ReviewSummary;
import com.shopmart.module.review.entity.ReviewStatus;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse addReview(Long userId, Long productId, ReviewRequest request);
    ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable);
    ReviewSummary getSummary(Long productId);
    ReviewResponse moderate(Long reviewId, ReviewStatus status);
}

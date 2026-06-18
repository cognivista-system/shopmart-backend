package com.shopmart.module.review.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.review.dto.ReviewRequest;
import com.shopmart.module.review.dto.ReviewResponse;
import com.shopmart.module.review.dto.ReviewSummary;
import com.shopmart.module.review.entity.ReviewStatus;
import com.shopmart.module.review.service.ReviewService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService service;

    // ---- Public reads (under /products/** => permitted GET) ----

    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.getProductReviews(productId, pageable));
    }

    @GetMapping("/products/{productId}/reviews/summary")
    public ApiResponse<ReviewSummary> getSummary(@PathVariable Long productId) {
        return ApiResponse.ok(service.getSummary(productId));
    }

    // ---- Authenticated writes ----

    @PostMapping("/products/{productId}/reviews")
    public ApiResponse<ReviewResponse> addReview(@PathVariable Long productId,
                                                 @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Review submitted",
                service.addReview(SecurityUtils.currentUserId(), productId, request));
    }

    @PutMapping("/reviews/{id}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable Long id,
                                                    @Valid @RequestBody ReviewRequest request) {
        return ApiResponse.ok("Review updated",
                service.updateReview(SecurityUtils.currentUserId(), id, request));
    }

    @DeleteMapping("/reviews/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        service.deleteReview(SecurityUtils.currentUserId(), id);
        return ApiResponse.message("Review deleted");
    }

    // ---- Admin moderation ----

    @PatchMapping("/reviews/{id}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> moderate(@PathVariable Long id, @RequestParam ReviewStatus status) {
        return ApiResponse.ok("Review moderated", service.moderate(id, status));
    }
}

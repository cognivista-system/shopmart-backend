package com.shopmart.module.review.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.order.repository.OrderItemRepository;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.review.dto.ReviewRequest;
import com.shopmart.module.review.dto.ReviewResponse;
import com.shopmart.module.review.dto.ReviewSummary;
import com.shopmart.module.review.entity.Review;
import com.shopmart.module.review.entity.ReviewStatus;
import com.shopmart.module.review.mapper.ReviewMapper;
import com.shopmart.module.review.repository.ReviewRepository;
import com.shopmart.module.review.service.ReviewService;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(Long userId, Long productId, ReviewRequest request) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new ConflictException("You have already reviewed this product");
        }
        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());
        review.setStatus(ReviewStatus.APPROVED);
        review.setVerifiedPurchase(orderItemRepository.existsForUserAndProduct(userId, productId));
        Review saved = reviewRepository.save(review);

        recalculateProductRating(productId);
        return ReviewMapper.toResponse(saved, userName(userId));
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setComment(request.comment());
        Review saved = reviewRepository.save(review);

        recalculateProductRating(review.getProductId());
        return ReviewMapper.toResponse(saved, userName(userId));
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        Long productId = review.getProductId();
        reviewRepository.delete(review);
        recalculateProductRating(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository
                .findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable)
                .map(r -> ReviewMapper.toResponse(r, userName(r.getUserId())));
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummary getSummary(Long productId) {
        double avg = reviewRepository.averageRating(productId);
        long count = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        return new ReviewSummary(productId,
                BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP).doubleValue(), count);
    }

    @Override
    @Transactional
    public ReviewResponse moderate(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
        review.setStatus(status);
        Review saved = reviewRepository.save(review);
        recalculateProductRating(review.getProductId());
        return ReviewMapper.toResponse(saved, userName(review.getUserId()));
    }

    // ---- helpers ----

    private void recalculateProductRating(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return;
        double avg = reviewRepository.averageRating(productId);
        long count = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        product.setRatingAverage(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        product.setRatingCount((int) count);
        productRepository.save(product);
    }

    private String userName(Long userId) {
        return userRepository.findById(userId).map(User::getName).orElse("Anonymous");
    }
}

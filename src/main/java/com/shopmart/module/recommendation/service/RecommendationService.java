package com.shopmart.module.recommendation.service;

import com.shopmart.module.product.dto.ProductSummary;

import java.util.List;

public interface RecommendationService {
    List<ProductSummary> similar(Long productId, int limit);
    List<ProductSummary> frequentlyBoughtTogether(Long productId, int limit);
    List<ProductSummary> trending(int limit);
    List<ProductSummary> recentlyViewed(Long userId, int limit);
    List<ProductSummary> forYou(Long userId, int limit);
    void recordView(Long userId, Long productId);
}

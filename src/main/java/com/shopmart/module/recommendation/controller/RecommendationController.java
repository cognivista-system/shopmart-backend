package com.shopmart.module.recommendation.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.recommendation.service.RecommendationService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Recommendations")
public class RecommendationController {

    private final RecommendationService service;

    // ---- Product-scoped (public; under /products/** GET) ----

    @GetMapping("/products/{id}/similar")
    public ApiResponse<List<ProductSummary>> similar(@PathVariable Long id,
                                                     @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok(service.similar(id, limit));
    }

    @GetMapping("/products/{id}/frequently-bought-together")
    public ApiResponse<List<ProductSummary>> frequentlyBoughtTogether(@PathVariable Long id,
                                                                      @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok(service.frequentlyBoughtTogether(id, limit));
    }

    @PostMapping("/products/{id}/view")
    public ApiResponse<Void> recordView(@PathVariable Long id) {
        service.recordView(SecurityUtils.currentUserIdOrNull(), id);
        return ApiResponse.message("View recorded");
    }

    // ---- Recommendation feeds ----

    @GetMapping("/recommendations/trending")
    public ApiResponse<List<ProductSummary>> trending(@RequestParam(defaultValue = "12") int limit) {
        return ApiResponse.ok(service.trending(limit));
    }

    @GetMapping("/recommendations/recently-viewed")
    public ApiResponse<List<ProductSummary>> recentlyViewed(@RequestParam(defaultValue = "12") int limit) {
        return ApiResponse.ok(service.recentlyViewed(SecurityUtils.currentUserId(), limit));
    }

    @GetMapping("/recommendations/for-you")
    public ApiResponse<List<ProductSummary>> forYou(@RequestParam(defaultValue = "12") int limit) {
        return ApiResponse.ok(service.forYou(SecurityUtils.currentUserId(), limit));
    }
}

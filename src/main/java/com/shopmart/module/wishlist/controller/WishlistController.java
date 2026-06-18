package com.shopmart.module.wishlist.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.wishlist.dto.WishlistItemResponse;
import com.shopmart.module.wishlist.service.WishlistService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist")
public class WishlistController {

    private final WishlistService service;

    @GetMapping
    public ApiResponse<List<WishlistItemResponse>> get() {
        return ApiResponse.ok(service.getWishlist(SecurityUtils.currentUserId()));
    }

    @PostMapping("/{productId}")
    public ApiResponse<List<WishlistItemResponse>> add(@PathVariable Long productId) {
        return ApiResponse.ok("Added to wishlist", service.add(SecurityUtils.currentUserId(), productId));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<List<WishlistItemResponse>> remove(@PathVariable Long productId) {
        return ApiResponse.ok("Removed from wishlist", service.remove(SecurityUtils.currentUserId(), productId));
    }

    @PostMapping("/{productId}/move-to-cart")
    public ApiResponse<Void> moveToCart(@PathVariable Long productId) {
        service.moveToCart(SecurityUtils.currentUserId(), productId);
        return ApiResponse.message("Moved to cart");
    }

    @DeleteMapping
    public ApiResponse<Void> clear() {
        service.clear(SecurityUtils.currentUserId());
        return ApiResponse.message("Wishlist cleared");
    }
}

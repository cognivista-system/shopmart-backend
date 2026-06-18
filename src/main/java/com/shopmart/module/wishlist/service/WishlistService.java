package com.shopmart.module.wishlist.service;

import com.shopmart.module.wishlist.dto.WishlistItemResponse;

import java.util.List;

public interface WishlistService {
    List<WishlistItemResponse> getWishlist(Long userId);
    List<WishlistItemResponse> add(Long userId, Long productId);
    List<WishlistItemResponse> remove(Long userId, Long productId);
    void moveToCart(Long userId, Long productId);
    void clear(Long userId);
}

package com.shopmart.module.cart.service;

import com.shopmart.module.cart.dto.AddToCartRequest;
import com.shopmart.module.cart.dto.CartResponse;
import com.shopmart.module.cart.dto.UpdateCartItemRequest;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItem(Long userId, AddToCartRequest request);
    CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartResponse removeItem(Long userId, Long itemId);
    void clear(Long userId);
}

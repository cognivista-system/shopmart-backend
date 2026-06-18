package com.shopmart.module.cart.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.cart.dto.AddToCartRequest;
import com.shopmart.module.cart.dto.CartResponse;
import com.shopmart.module.cart.dto.UpdateCartItemRequest;
import com.shopmart.module.cart.service.CartService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
public class CartController {

    private final CartService service;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.ok(service.getCart(SecurityUtils.currentUserId()));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> add(@Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.ok("Item added to cart", service.addItem(SecurityUtils.currentUserId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> update(@PathVariable Long itemId,
                                            @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.ok(service.updateItem(SecurityUtils.currentUserId(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> remove(@PathVariable Long itemId) {
        return ApiResponse.ok("Item removed", service.removeItem(SecurityUtils.currentUserId(), itemId));
    }

    @DeleteMapping
    public ApiResponse<Void> clear() {
        service.clear(SecurityUtils.currentUserId());
        return ApiResponse.message("Cart cleared");
    }
}

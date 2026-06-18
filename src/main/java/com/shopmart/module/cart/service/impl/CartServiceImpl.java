package com.shopmart.module.cart.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.cart.dto.*;
import com.shopmart.module.cart.entity.Cart;
import com.shopmart.module.cart.entity.CartItem;
import com.shopmart.module.cart.repository.CartRepository;
import com.shopmart.module.cart.service.CartService;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse getCart(Long userId) {
        return toResponse(getOrCreate(userId));
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreate(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.productId()));
        if (product.getStock() < request.quantity()) {
            throw new BadRequestException("Not enough stock for " + product.getName());
        }

        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId())
                        && java.util.Objects.equals(i.getVariantId(), request.variantId()))
                .findFirst().orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
        } else {
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setVariantId(request.variantId());
            item.setQuantity(request.quantity());
            item.setUnitPrice(effectivePrice(product));
            cart.addItem(item);
        }
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreate(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", itemId));
        item.setQuantity(request.quantity());
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreate(userId);
        boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("Cart item", "id", itemId);
        }
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clear(Long userId) {
        Cart cart = getOrCreate(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // ---- helpers ----

    private Cart getOrCreate(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            return cartRepository.save(cart);
        });
    }

    private BigDecimal effectivePrice(Product p) {
        return p.getSalePrice() != null ? p.getSalePrice() : p.getPrice();
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            String name = product != null ? product.getName() : "Unavailable";
            String thumb = product != null && !product.getImages().isEmpty()
                    ? product.getImages().get(0).getUrl() : null;
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return new CartItemResponse(item.getId(), item.getProductId(), item.getVariantId(),
                    name, thumb, item.getQuantity(), item.getUnitPrice(), lineTotal);
        }).toList();

        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), items, totalItems, subtotal);
    }
}

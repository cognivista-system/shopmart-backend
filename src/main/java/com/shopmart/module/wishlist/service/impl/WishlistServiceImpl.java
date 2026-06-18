package com.shopmart.module.wishlist.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.cart.dto.AddToCartRequest;
import com.shopmart.module.cart.service.CartService;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.wishlist.dto.WishlistItemResponse;
import com.shopmart.module.wishlist.entity.WishlistItem;
import com.shopmart.module.wishlist.repository.WishlistItemRepository;
import com.shopmart.module.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository repository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return repository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public List<WishlistItemResponse> add(Long userId, Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        if (!repository.existsByUserIdAndProductId(userId, productId)) {
            WishlistItem item = new WishlistItem();
            item.setUserId(userId);
            item.setProductId(productId);
            repository.save(item);
        }
        return getWishlist(userId);
    }

    @Override
    @Transactional
    public List<WishlistItemResponse> remove(Long userId, Long productId) {
        repository.findByUserIdAndProductId(userId, productId).ifPresent(repository::delete);
        return getWishlist(userId);
    }

    @Override
    @Transactional
    public void moveToCart(Long userId, Long productId) {
        WishlistItem item = repository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item", "productId", productId));
        cartService.addItem(userId, new AddToCartRequest(productId, null, 1));
        repository.delete(item);
    }

    @Override
    @Transactional
    public void clear(Long userId) {
        repository.deleteByUserId(userId);
    }

    private WishlistItemResponse toResponse(WishlistItem item) {
        Product p = productRepository.findById(item.getProductId()).orElse(null);
        if (p == null) {
            return new WishlistItemResponse(item.getId(), item.getProductId(),
                    "Unavailable", null, null, null, null, false);
        }
        String thumb = p.getImages().isEmpty() ? null : p.getImages().get(0).getUrl();
        return new WishlistItemResponse(item.getId(), p.getId(), p.getName(), p.getSlug(),
                thumb, p.getPrice(), p.getSalePrice(), p.getStock() > 0);
    }
}

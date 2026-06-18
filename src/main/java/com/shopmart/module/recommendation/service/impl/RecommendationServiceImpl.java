package com.shopmart.module.recommendation.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.order.repository.OrderItemRepository;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.mapper.ProductMapper;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.recommendation.entity.ProductView;
import com.shopmart.module.recommendation.repository.ProductViewRepository;
import com.shopmart.module.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductViewRepository productViewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> similar(Long productId, int limit) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (product.getCategory() == null) {
            return trending(limit);
        }
        return productRepository
                .findTop12ByCategory_IdAndStatusAndIdNot(product.getCategory().getId(), ProductStatus.ACTIVE, productId)
                .stream()
                .limit(cap(limit))
                .map(ProductMapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> frequentlyBoughtTogether(Long productId, int limit) {
        List<Long> ids = orderItemRepository.coPurchased(productId, PageRequest.of(0, cap(limit))).stream()
                .map(row -> (Long) row[0])
                .toList();
        return summariesInOrder(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> trending(int limit) {
        List<Long> ids = orderItemRepository.topProducts(PageRequest.of(0, cap(limit))).stream()
                .map(row -> (Long) row[0])
                .toList();
        return summariesInOrder(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> recentlyViewed(Long userId, int limit) {
        List<Long> ids = productViewRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ProductView::getProductId)
                .distinct()
                .limit(cap(limit))
                .toList();
        return summariesInOrder(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummary> forYou(Long userId, int limit) {
        // Derive interest from recently viewed products' categories
        List<Long> viewedIds = productViewRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ProductView::getProductId)
                .distinct()
                .toList();
        if (viewedIds.isEmpty()) {
            return trending(limit);
        }
        List<Product> viewed = productRepository.findByIdInAndStatus(viewedIds, ProductStatus.ACTIVE);
        LinkedHashSet<Long> categoryIds = viewed.stream()
                .filter(p -> p.getCategory() != null)
                .map(p -> p.getCategory().getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        LinkedHashSet<ProductSummary> result = new LinkedHashSet<>();
        for (Long catId : categoryIds) {
            productRepository.findTop12ByCategory_IdAndStatusAndIdNot(catId, ProductStatus.ACTIVE, -1L).stream()
                    .filter(p -> !viewedIds.contains(p.getId()))
                    .map(ProductMapper::toSummary)
                    .forEach(result::add);
            if (result.size() >= cap(limit)) break;
        }
        if (result.isEmpty()) {
            return trending(limit);
        }
        return result.stream().limit(cap(limit)).toList();
    }

    @Override
    @Transactional
    public void recordView(Long userId, Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        ProductView view = new ProductView();
        view.setUserId(userId);
        view.setProductId(productId);
        productViewRepository.save(view);
    }

    // ---- helpers ----

    private List<ProductSummary> summariesInOrder(List<Long> ids) {
        if (ids.isEmpty()) return List.of();
        Map<Long, Product> byId = productRepository.findByIdInAndStatus(ids, ProductStatus.ACTIVE).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        List<ProductSummary> ordered = new ArrayList<>();
        for (Long id : ids) {
            Product p = byId.get(id);
            if (p != null) ordered.add(ProductMapper.toSummary(p));
        }
        return ordered;
    }

    private int cap(int limit) {
        return Math.max(1, Math.min(limit, 50));
    }
}

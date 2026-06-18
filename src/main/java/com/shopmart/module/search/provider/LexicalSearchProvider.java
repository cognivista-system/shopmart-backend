package com.shopmart.module.search.provider;

import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "app.search.provider", havingValue = "lexical", matchIfMissing = true)
public class LexicalSearchProvider implements SearchProvider {

    private final ProductRepository productRepository;

    @Override
    public Page<Product> search(String q, Long categoryId, Long brandId, Pageable pageable) {
        return productRepository.searchActive(q == null ? "" : q.trim(), categoryId, brandId, pageable);
    }

    @Override
    public List<String> suggest(String prefix, int limit) {
        if (prefix == null || prefix.isBlank()) return List.of();
        return productRepository
                .findTop8ByNameContainingIgnoreCaseAndStatus(prefix.trim(), ProductStatus.ACTIVE)
                .stream()
                .map(Product::getName)
                .distinct()
                .limit(limit)
                .toList();
    }
}

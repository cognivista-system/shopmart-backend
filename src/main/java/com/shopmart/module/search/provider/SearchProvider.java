package com.shopmart.module.search.provider;

import com.shopmart.module.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Pluggable search backend. The default {@link LexicalSearchProvider} does
 * relevance-ranked keyword search over products. A future embedding/vector
 * provider can implement this same interface (mark it @Primary) without any
 * change to SearchService or the controller — mirrors the PaymentGateway pattern.
 */
public interface SearchProvider {
    Page<Product> search(String q, Long categoryId, Long brandId, Pageable pageable);
    List<String> suggest(String prefix, int limit);
}

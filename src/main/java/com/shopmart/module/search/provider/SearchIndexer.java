package com.shopmart.module.search.provider;

import com.shopmart.module.product.entity.Product;

/** Hook for keeping an external search index in sync. No-op unless Elasticsearch is enabled. */
public interface SearchIndexer {
    void index(Product product);
    void delete(Long productId);
    long reindexAll();
}

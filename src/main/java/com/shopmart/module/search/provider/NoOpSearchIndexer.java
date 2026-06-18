package com.shopmart.module.search.provider;

import com.shopmart.module.product.entity.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Active unless app.search.provider=elasticsearch. Does nothing. */
@Component
@ConditionalOnProperty(name = "app.search.provider", havingValue = "lexical", matchIfMissing = true)
public class NoOpSearchIndexer implements SearchIndexer {
    @Override public void index(Product product) { }
    @Override public void delete(Long productId) { }
    @Override public long reindexAll() { return 0; }
}

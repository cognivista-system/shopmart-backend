package com.shopmart.module.search.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Keeps the Elasticsearch "products" index in sync. Active when app.search.provider=elasticsearch. */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.search.provider", havingValue = "elasticsearch")
public class ElasticsearchIndexer implements SearchIndexer {

    private final ProductRepository productRepository;
    private final ElasticsearchClient es;
    private final ObjectMapper om;

    public ElasticsearchIndexer(ProductRepository productRepository,
                                ObjectMapper objectMapper,
                                @Value("${app.search.elasticsearch.url:http://localhost:9200}") String url) {
        this.productRepository = productRepository;
        this.om = objectMapper;
        this.es = new ElasticsearchClient(objectMapper, url);
    }

    @Override
    public void index(Product p) {
        try {
            ObjectNode doc = om.createObjectNode();
            doc.put("name", p.getName());
            doc.put("description", p.getDescription());
            doc.put("status", p.getStatus() != null ? p.getStatus().name() : null);
            if (p.getCategory() != null) {
                doc.put("categoryId", p.getCategory().getId());
                doc.put("categoryName", p.getCategory().getName());
            }
            if (p.getBrand() != null) {
                doc.put("brandId", p.getBrand().getId());
                doc.put("brandName", p.getBrand().getName());
            }
            es.send("PUT", "/" + ElasticsearchSearchProvider.INDEX + "/_doc/" + p.getId(), om.writeValueAsString(doc));
        } catch (Exception e) {
            log.warn("[ES] index product {} failed: {}", p.getId(), e.getMessage());
        }
    }

    @Override
    public void delete(Long productId) {
        try {
            es.send("DELETE", "/" + ElasticsearchSearchProvider.INDEX + "/_doc/" + productId, null);
        } catch (Exception e) {
            log.warn("[ES] delete product {} failed: {}", productId, e.getMessage());
        }
    }

    @Override
    public long reindexAll() {
        long n = 0;
        for (Product p : productRepository.findAll()) {
            index(p);
            n++;
        }
        log.info("[ES] reindexed {} products", n);
        return n;
    }
}

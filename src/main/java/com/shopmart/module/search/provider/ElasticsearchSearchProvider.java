package com.shopmart.module.search.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch-backed product search. Active when app.search.provider=elasticsearch.
 * Queries ES for matching product ids, then loads the entities from the DB so the
 * response shape is identical to the lexical provider. Falls back to an empty page on error.
 * Indexing is handled by {@link ElasticsearchIndexer} (reindex endpoint / write hooks).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.search.provider", havingValue = "elasticsearch")
public class ElasticsearchSearchProvider implements SearchProvider {

    static final String INDEX = "products";

    private final ProductRepository productRepository;
    private final ElasticsearchClient es;
    private final ObjectMapper om;

    public ElasticsearchSearchProvider(ProductRepository productRepository,
                                       ObjectMapper objectMapper,
                                       @Value("${app.search.elasticsearch.url:http://localhost:9200}") String url) {
        this.productRepository = productRepository;
        this.om = objectMapper;
        this.es = new ElasticsearchClient(objectMapper, url);
    }

    @Override
    public Page<Product> search(String q, Long categoryId, Long brandId, Pageable pageable) {
        try {
            List<String> filters = new ArrayList<>();
            filters.add("{\"term\":{\"status\":\"ACTIVE\"}}");
            if (categoryId != null) filters.add("{\"term\":{\"categoryId\":" + categoryId + "}}");
            if (brandId != null) filters.add("{\"term\":{\"brandId\":" + brandId + "}}");

            String must = (q == null || q.isBlank())
                    ? "{\"match_all\":{}}"
                    : "{\"multi_match\":{\"query\":" + om.writeValueAsString(q)
                      + ",\"fields\":[\"name^3\",\"brandName^2\",\"categoryName\",\"description\"]}}";

            String body = "{\"from\":" + pageable.getOffset() + ",\"size\":" + pageable.getPageSize()
                    + ",\"_source\":false,\"query\":{\"bool\":{\"must\":" + must
                    + ",\"filter\":[" + String.join(",", filters) + "]}}}";

            JsonNode resp = es.send("POST", "/" + INDEX + "/_search", body);
            JsonNode hits = resp.path("hits").path("hits");
            List<Long> ids = new ArrayList<>();
            for (JsonNode h : hits) ids.add(Long.valueOf(h.path("_id").asText()));
            long total = resp.path("hits").path("total").path("value").asLong(ids.size());

            if (ids.isEmpty()) return new PageImpl<>(List.of(), pageable, 0);

            Map<Long, Product> byId = new LinkedHashMap<>();
            for (Product p : productRepository.findByIdInAndStatus(ids, ProductStatus.ACTIVE)) {
                byId.put(p.getId(), p);
            }
            List<Product> ordered = new ArrayList<>();
            for (Long id : ids) if (byId.containsKey(id)) ordered.add(byId.get(id));
            return new PageImpl<>(ordered, pageable, total);
        } catch (Exception e) {
            log.error("[ES] search failed: {}", e.getMessage());
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    public List<String> suggest(String prefix, int limit) {
        try {
            String body = "{\"size\":" + Math.max(1, limit)
                    + ",\"_source\":[\"name\"],\"query\":{\"match_phrase_prefix\":{\"name\":"
                    + om.writeValueAsString(prefix) + "}}}";
            JsonNode resp = es.send("POST", "/" + INDEX + "/_search", body);
            List<String> out = new ArrayList<>();
            for (JsonNode h : resp.path("hits").path("hits")) {
                String name = h.path("_source").path("name").asText(null);
                if (name != null && !out.contains(name)) out.add(name);
            }
            out.sort(Comparator.naturalOrder());
            return out;
        } catch (Exception e) {
            log.error("[ES] suggest failed: {}", e.getMessage());
            return List.of();
        }
    }
}

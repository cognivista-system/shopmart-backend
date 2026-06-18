package com.shopmart.module.search.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.mapper.ProductMapper;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.search.dto.FacetCount;
import com.shopmart.module.search.dto.SearchResultResponse;
import com.shopmart.module.search.entity.SearchLog;
import com.shopmart.module.search.provider.SearchProvider;
import com.shopmart.module.search.repository.SearchLogRepository;
import com.shopmart.module.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchProvider searchProvider;
    private final ProductRepository productRepository;
    private final SearchLogRepository searchLogRepository;

    @Override
    @Transactional
    public SearchResultResponse search(String q, Long categoryId, Long brandId, Long userId, Pageable pageable) {
        String query = q == null ? "" : q.trim();

        // Provider bakes relevance ordering into the query; use an unsorted page request.
        Pageable pr = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> page = searchProvider.search(query, categoryId, brandId, pr);

        PageResponse<ProductSummary> results = PageResponse.from(page.map(ProductMapper::toSummary));
        List<FacetCount> categoryFacets = toFacets(productRepository.facetByCategory(query));
        List<FacetCount> brandFacets = toFacets(productRepository.facetByBrand(query));
        List<String> suggestions = query.isBlank() ? List.of() : searchProvider.suggest(query, 5);

        if (!query.isBlank()) {
            SearchLog log = new SearchLog();
            log.setUserId(userId);
            log.setQueryText(query);
            log.setResultCount(page.getTotalElements());
            searchLogRepository.save(log);
        }

        return new SearchResultResponse(query, results, categoryFacets, brandFacets, suggestions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> suggest(String prefix, int limit) {
        return searchProvider.suggest(prefix, Math.max(1, Math.min(limit, 10)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> trending(int limit) {
        Instant since = Instant.now().minus(30, ChronoUnit.DAYS);
        int n = Math.max(1, Math.min(limit, 20));
        return searchLogRepository.trending(since, PageRequest.of(0, n)).stream()
                .map(row -> (String) row[0])
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> history(Long userId) {
        return searchLogRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(SearchLog::getQueryText)
                .distinct()
                .toList();
    }

    @Override
    @Transactional
    public void clearHistory(Long userId) {
        searchLogRepository.deleteByUserId(userId);
    }

    private List<FacetCount> toFacets(List<Object[]> rows) {
        return rows.stream()
                .map(r -> new FacetCount((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }
}

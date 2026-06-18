package com.shopmart.module.search.service;

import com.shopmart.module.search.dto.SearchResultResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    SearchResultResponse search(String q, Long categoryId, Long brandId, Long userId, Pageable pageable);
    List<String> suggest(String prefix, int limit);
    List<String> trending(int limit);
    List<String> history(Long userId);
    void clearHistory(Long userId);
}

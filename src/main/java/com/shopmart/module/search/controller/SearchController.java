package com.shopmart.module.search.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.search.dto.SearchResultResponse;
import com.shopmart.module.search.provider.SearchIndexer;
import com.shopmart.module.search.service.SearchService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search")
public class SearchController {

    private final SearchService service;
    private final SearchIndexer searchIndexer;

    @GetMapping
    public ApiResponse<SearchResultResponse> search(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        Long userId = SecurityUtils.currentUserIdOrNull();
        return ApiResponse.ok(service.search(q, categoryId, brandId, userId, pageable));
    }

    @GetMapping("/suggest")
    public ApiResponse<List<String>> suggest(@RequestParam String q,
                                             @RequestParam(defaultValue = "8") int limit) {
        return ApiResponse.ok(service.suggest(q, limit));
    }

    @GetMapping("/trending")
    public ApiResponse<List<String>> trending(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(service.trending(limit));
    }

    @GetMapping("/history")
    public ApiResponse<List<String>> history() {
        return ApiResponse.ok(service.history(SecurityUtils.currentUserId()));
    }

    @DeleteMapping("/history")
    public ApiResponse<Void> clearHistory() {
        service.clearHistory(SecurityUtils.currentUserId());
        return ApiResponse.message("Search history cleared");
    }

    /** Rebuild the external search index (no-op unless app.search.provider=elasticsearch). */
    @PostMapping("/admin/reindex")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> reindex() {
        long n = searchIndexer.reindexAll();
        return ApiResponse.ok("Reindex complete", Map.of("indexed", n));
    }
}

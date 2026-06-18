package com.shopmart.module.blog.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.blog.dto.BlogPostRequest;
import com.shopmart.module.blog.dto.BlogPostResponse;
import com.shopmart.module.blog.dto.BlogPostSummary;
import org.springframework.data.domain.Pageable;

public interface BlogService {
    BlogPostResponse create(Long authorId, BlogPostRequest request);
    BlogPostResponse update(Long id, BlogPostRequest request);
    void delete(Long id);
    BlogPostResponse getBySlug(String slug);          // published only (public)
    BlogPostResponse getById(Long id);                // admin (any status)
    PageResponse<BlogPostSummary> listPublished(String tag, Pageable pageable);
    PageResponse<BlogPostResponse> listAll(Pageable pageable);   // admin
}

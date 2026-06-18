package com.shopmart.module.blog.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.blog.dto.BlogPostRequest;
import com.shopmart.module.blog.dto.BlogPostResponse;
import com.shopmart.module.blog.dto.BlogPostSummary;
import com.shopmart.module.blog.service.BlogService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
@Tag(name = "Blogs")
public class BlogController {

    private final BlogService service;

    // ---- Public ----

    @GetMapping
    public ApiResponse<PageResponse<BlogPostSummary>> listPublished(
            @RequestParam(required = false) String tag,
            @PageableDefault(size = 10, sort = "publishedAt") Pageable pageable) {
        return ApiResponse.ok(service.listPublished(tag, pageable));
    }

    @GetMapping("/{slug}")
    public ApiResponse<BlogPostResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok(service.getBySlug(slug));
    }

    // ---- Admin ----

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<BlogPostResponse>> listAll(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.listAll(pageable));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BlogPostResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BlogPostResponse> create(@Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.ok("Post created", service.create(SecurityUtils.currentUserId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BlogPostResponse> update(@PathVariable Long id, @Valid @RequestBody BlogPostRequest request) {
        return ApiResponse.ok("Post updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Post deleted");
    }
}

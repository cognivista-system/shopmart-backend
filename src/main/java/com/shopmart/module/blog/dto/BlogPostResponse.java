package com.shopmart.module.blog.dto;

import java.time.Instant;
import java.util.Set;

public record BlogPostResponse(
        Long id,
        String title,
        String slug,
        String excerpt,
        String content,
        String coverImage,
        Long authorId,
        String status,
        Set<String> tags,
        Instant publishedAt,
        Instant createdAt
) {}

package com.shopmart.module.blog.dto;

import java.time.Instant;
import java.util.Set;

public record BlogPostSummary(
        Long id,
        String title,
        String slug,
        String excerpt,
        String coverImage,
        Set<String> tags,
        Instant publishedAt
) {}

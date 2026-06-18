package com.shopmart.module.blog.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record BlogPostRequest(
        @NotBlank String title,
        String excerpt,
        String content,
        String coverImage,
        String status,          // DRAFT | PUBLISHED
        Set<String> tags
) {}

package com.shopmart.module.banner.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record BannerRequest(
        @NotBlank String title,
        String subtitle,
        @NotBlank String imageUrl,
        String linkUrl,
        Integer position,
        Boolean active,
        Instant startsAt,
        Instant endsAt
) {}

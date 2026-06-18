package com.shopmart.module.banner.dto;

import java.time.Instant;

public record BannerResponse(
        Long id,
        String title,
        String subtitle,
        String imageUrl,
        String linkUrl,
        int position,
        boolean active,
        Instant startsAt,
        Instant endsAt
) {}

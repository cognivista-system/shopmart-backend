package com.shopmart.module.banner.mapper;

import com.shopmart.module.banner.dto.BannerResponse;
import com.shopmart.module.banner.entity.Banner;

public final class BannerMapper {
    private BannerMapper() {}

    public static BannerResponse toResponse(Banner b) {
        return new BannerResponse(b.getId(), b.getTitle(), b.getSubtitle(), b.getImageUrl(),
                b.getLinkUrl(), b.getPosition(), b.isActive(), b.getStartsAt(), b.getEndsAt());
    }
}

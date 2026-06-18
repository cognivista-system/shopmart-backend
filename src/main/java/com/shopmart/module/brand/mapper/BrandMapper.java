package com.shopmart.module.brand.mapper;

import com.shopmart.module.brand.dto.BrandResponse;
import com.shopmart.module.brand.entity.Brand;

public final class BrandMapper {
    private BrandMapper() {}

    public static BrandResponse toResponse(Brand b) {
        return new BrandResponse(b.getId(), b.getName(), b.getSlug(), b.getDescription(),
                b.getLogoUrl(), b.getBannerUrl(), b.getMetaTitle(), b.getMetaDescription(), b.isActive());
    }
}

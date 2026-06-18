package com.shopmart.module.category.mapper;

import com.shopmart.module.category.dto.CategoryResponse;
import com.shopmart.module.category.entity.Category;

public final class CategoryMapper {
    private CategoryMapper() {}

    public static CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getDescription(),
                c.getBannerUrl(), c.getMetaTitle(), c.getMetaDescription(), c.isActive());
    }
}

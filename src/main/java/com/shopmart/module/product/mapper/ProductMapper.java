package com.shopmart.module.product.mapper;

import com.shopmart.module.product.dto.ProductImageDto;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.dto.ProductVariantDto;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductImage;
import com.shopmart.module.product.entity.ProductVariant;

import java.util.List;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getSlug(), p.getDescription(), p.getSku(),
                p.getPrice(), p.getSalePrice(), p.getStock(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getCategory() != null ? p.getCategory().getName() : null,
                p.getBrand() != null ? p.getBrand().getId() : null,
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getStatus().name(), p.isFeatured(),
                p.getRatingAverage(), p.getRatingCount(),
                p.getMetaTitle(), p.getMetaDescription(),
                p.getImages().stream().map(ProductMapper::toImageDto).toList(),
                p.getVariants().stream().map(ProductMapper::toVariantDto).toList(),
                p.getRejectionReason()
        );
    }

    public static ProductSummary toSummary(Product p) {
        String thumb = p.getImages().isEmpty() ? null : p.getImages().get(0).getUrl();
        return new ProductSummary(
                p.getId(), p.getName(), p.getSlug(), p.getPrice(), p.getSalePrice(),
                thumb, p.getBrand() != null ? p.getBrand().getName() : null,
                p.getRatingAverage(), p.isFeatured()
        );
    }

    public static ProductImageDto toImageDto(ProductImage i) {
        return new ProductImageDto(i.getId(), i.getUrl(), i.getAlt(), i.getPosition());
    }

    public static ProductVariantDto toVariantDto(ProductVariant v) {
        return new ProductVariantDto(v.getId(), v.getSku(), v.getSize(), v.getColor(), v.getPrice(), v.getStock());
    }

    public static List<ProductSummary> toSummaryList(List<Product> products) {
        return products.stream().map(ProductMapper::toSummary).toList();
    }
}

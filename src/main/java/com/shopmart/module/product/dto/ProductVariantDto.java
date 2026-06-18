package com.shopmart.module.product.dto;

import java.math.BigDecimal;

public record ProductVariantDto(Long id, String sku, String size, String color, BigDecimal price, int stock) {}

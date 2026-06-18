package com.shopmart.module.vendor.dto;

import java.math.BigDecimal;

public record VendorResponse(
        Long id,
        Long userId,
        String storeName,
        String slug,
        String description,
        String logoUrl,
        String status,
        BigDecimal commissionRate,
        String contactEmail,
        String contactPhone
) {}

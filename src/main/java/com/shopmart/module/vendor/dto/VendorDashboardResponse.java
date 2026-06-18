package com.shopmart.module.vendor.dto;

import java.math.BigDecimal;

public record VendorDashboardResponse(
        Long vendorId,
        String storeName,
        String status,
        long totalProducts,
        long activeProducts,
        long pendingProducts,
        BigDecimal grossSales,
        BigDecimal commissionRate,
        BigDecimal commission,
        BigDecimal netEarnings,
        BigDecimal totalPaidOut,
        BigDecimal pendingBalance
) {}

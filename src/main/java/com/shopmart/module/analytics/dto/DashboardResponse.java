package com.shopmart.module.analytics.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        BigDecimal totalRevenue,
        long totalOrders,
        long pendingOrders,
        long totalCustomers,
        long totalProducts,
        long lowStockCount
) {}

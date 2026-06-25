package com.shopmart.module.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardResponse(
        long totalProducts,
        long pendingProducts,
        long activeProducts,
        long totalOrders,
        long pendingOrders,
        BigDecimal revenue,
        long totalCustomers
) {}

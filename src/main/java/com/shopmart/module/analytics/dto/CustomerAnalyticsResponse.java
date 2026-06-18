package com.shopmart.module.analytics.dto;

import java.math.BigDecimal;

public record CustomerAnalyticsResponse(
        long totalCustomers,
        long customersWithOrders,
        long repeatCustomers,
        BigDecimal repeatRatePct,
        BigDecimal avgOrderValue,
        long newCustomers30d
) {}

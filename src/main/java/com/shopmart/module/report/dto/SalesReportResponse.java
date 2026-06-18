package com.shopmart.module.report.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SalesReportResponse(
        Instant from,
        Instant to,
        BigDecimal totalRevenue,
        long totalOrders,
        List<SalesReportRow> daily
) {}

package com.shopmart.module.report.dto;

import java.time.Instant;
import java.util.List;

public record OrderReportResponse(
        Instant from,
        Instant to,
        long totalOrders,
        List<StatusRow> byStatus
) {}

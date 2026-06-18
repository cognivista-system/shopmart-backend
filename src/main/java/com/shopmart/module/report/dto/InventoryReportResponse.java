package com.shopmart.module.report.dto;

import java.math.BigDecimal;
import java.util.List;

public record InventoryReportResponse(
        long totalProducts,
        BigDecimal totalStockValue,
        long lowStockCount,
        List<LowStockRow> lowStock
) {}

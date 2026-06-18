package com.shopmart.module.report.dto;

import java.math.BigDecimal;

public record RevenueRow(String label, BigDecimal revenue, long unitsSold) {}

package com.shopmart.module.report.dto;

import java.math.BigDecimal;

public record CustomerRow(Long userId, String name, long orders, BigDecimal totalSpent) {}

package com.shopmart.module.report.dto;

import java.math.BigDecimal;

public record CommissionRow(
        Long vendorId,
        String storeName,
        BigDecimal grossSales,
        BigDecimal commissionRate,
        BigDecimal commission,
        BigDecimal netEarnings
) {}

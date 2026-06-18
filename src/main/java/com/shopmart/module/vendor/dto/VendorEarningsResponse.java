package com.shopmart.module.vendor.dto;

import java.math.BigDecimal;

public record VendorEarningsResponse(
        Long vendorId,
        BigDecimal grossSales,
        BigDecimal commissionRate,
        BigDecimal commission,
        BigDecimal netEarnings,
        BigDecimal totalPaidOut,
        BigDecimal pendingBalance
) {}

package com.shopmart.module.analytics.dto;

import java.math.BigDecimal;

public record TopProduct(Long productId, String name, long unitsSold, BigDecimal revenue) {}

package com.shopmart.module.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesPoint(LocalDate date, BigDecimal revenue, long orders) {}

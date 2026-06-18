package com.shopmart.module.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesReportRow(LocalDate date, BigDecimal revenue, long orders) {}

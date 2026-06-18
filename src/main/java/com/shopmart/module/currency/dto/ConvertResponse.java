package com.shopmart.module.currency.dto;

import java.math.BigDecimal;

public record ConvertResponse(BigDecimal amount, String from, String to, BigDecimal converted, BigDecimal rate) {}

package com.shopmart.module.currency.dto;

import java.math.BigDecimal;

public record CurrencyResponse(String code, BigDecimal rate) {}

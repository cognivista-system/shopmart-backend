package com.shopmart.module.currency.service;

import com.shopmart.module.currency.dto.ConvertResponse;
import com.shopmart.module.currency.dto.CurrencyResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyService {
    String base();
    List<CurrencyResponse> list();
    BigDecimal rate(String code);
    ConvertResponse convert(BigDecimal amount, String from, String to);
}

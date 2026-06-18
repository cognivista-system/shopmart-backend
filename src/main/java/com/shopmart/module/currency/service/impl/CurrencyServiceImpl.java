package com.shopmart.module.currency.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.module.currency.dto.ConvertResponse;
import com.shopmart.module.currency.dto.CurrencyResponse;
import com.shopmart.module.currency.service.CurrencyService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prices are stored in the base currency; this service exposes the rate table and
 * converts on demand. Rates come from app.currency.rates (CODE:rate, relative to base).
 * Swap in a live HTTP rate feed later without changing callers.
 */
@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final String base;
    private final String ratesRaw;
    private final Map<String, BigDecimal> rates = new LinkedHashMap<>();

    public CurrencyServiceImpl(@Value("${app.currency.base:INR}") String base,
                               @Value("${app.currency.rates:INR:1}") String ratesRaw) {
        this.base = base.toUpperCase();
        this.ratesRaw = ratesRaw;
    }

    @PostConstruct
    void load() {
        for (String pair : ratesRaw.split(",")) {
            String[] kv = pair.trim().split(":");
            if (kv.length == 2) {
                try {
                    rates.put(kv[0].trim().toUpperCase(), new BigDecimal(kv[1].trim()));
                } catch (NumberFormatException ignored) {
                    log.warn("[CURRENCY] bad rate pair: {}", pair);
                }
            }
        }
        rates.putIfAbsent(base, BigDecimal.ONE);
        log.info("[CURRENCY] base={} rates={}", base, rates.keySet());
    }

    @Override
    public String base() {
        return base;
    }

    @Override
    public List<CurrencyResponse> list() {
        List<CurrencyResponse> out = new ArrayList<>();
        rates.forEach((k, v) -> out.add(new CurrencyResponse(k, v)));
        return out;
    }

    @Override
    public BigDecimal rate(String code) {
        BigDecimal r = rates.get(code == null ? base : code.toUpperCase());
        if (r == null) throw new BadRequestException("Unsupported currency: " + code);
        return r;
    }

    @Override
    public ConvertResponse convert(BigDecimal amount, String from, String to) {
        if (amount == null) throw new BadRequestException("amount is required");
        String f = (from == null || from.isBlank()) ? base : from.toUpperCase();
        String t = (to == null || to.isBlank()) ? base : to.toUpperCase();
        BigDecimal inBase = amount.divide(rate(f), 8, RoundingMode.HALF_UP);
        BigDecimal converted = inBase.multiply(rate(t)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal effRate = rate(t).divide(rate(f), 6, RoundingMode.HALF_UP);
        return new ConvertResponse(amount, f, t, converted, effRate);
    }
}

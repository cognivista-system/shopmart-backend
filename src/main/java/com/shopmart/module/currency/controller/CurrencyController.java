package com.shopmart.module.currency.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.currency.dto.ConvertResponse;
import com.shopmart.module.currency.dto.CurrencyResponse;
import com.shopmart.module.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
@Tag(name = "Currencies")
public class CurrencyController {

    private final CurrencyService service;

    @GetMapping
    public ApiResponse<List<CurrencyResponse>> list() {
        return ApiResponse.ok(service.list());
    }

    @GetMapping("/convert")
    public ApiResponse<ConvertResponse> convert(@RequestParam BigDecimal amount,
                                                @RequestParam(required = false) String from,
                                                @RequestParam(required = false) String to) {
        return ApiResponse.ok(service.convert(amount, from, to));
    }
}

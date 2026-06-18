package com.shopmart.module.loyalty.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.loyalty.dto.*;
import com.shopmart.module.loyalty.service.LoyaltyService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loyalty")
@RequiredArgsConstructor
@Tag(name = "Loyalty Points")
public class LoyaltyController {

    private final LoyaltyService service;

    @GetMapping("/me")
    public ApiResponse<LoyaltyBalanceResponse> balance() {
        return ApiResponse.ok(service.balance(SecurityUtils.currentUserId()));
    }

    @GetMapping("/me/transactions")
    public ApiResponse<List<LoyaltyTransactionResponse>> history() {
        return ApiResponse.ok(service.history(SecurityUtils.currentUserId()));
    }

    @PostMapping("/redeem")
    public ApiResponse<RedeemResponse> redeem(@Valid @RequestBody RedeemRequest request) {
        return ApiResponse.ok("Points redeemed", service.redeem(SecurityUtils.currentUserId(), request.points()));
    }

    @PostMapping("/admin/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<LoyaltyTransactionResponse> adjust(@Valid @RequestBody AdjustRequest request) {
        return ApiResponse.ok("Balance adjusted",
                service.adjust(request.userId(), request.points(), request.reason()));
    }
}

package com.shopmart.module.loyalty.service;

import com.shopmart.module.loyalty.dto.LoyaltyBalanceResponse;
import com.shopmart.module.loyalty.dto.LoyaltyTransactionResponse;
import com.shopmart.module.loyalty.dto.RedeemResponse;

import java.math.BigDecimal;
import java.util.List;

public interface LoyaltyService {
    LoyaltyBalanceResponse balance(Long userId);
    List<LoyaltyTransactionResponse> history(Long userId);
    RedeemResponse redeem(Long userId, int points);

    /** Best-effort: award points for a placed order. Safe to call inside order creation. */
    void earnForOrder(Long userId, Long orderId, BigDecimal orderTotal);

    /** Admin manual adjustment (positive or negative). */
    LoyaltyTransactionResponse adjust(Long userId, int points, String reason);
}

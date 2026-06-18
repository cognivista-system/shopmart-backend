package com.shopmart.module.loyalty.dto;

import java.math.BigDecimal;

public record LoyaltyBalanceResponse(int balance, BigDecimal pointValue, BigDecimal redeemableValue) {}

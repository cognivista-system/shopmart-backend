package com.shopmart.module.loyalty.dto;

import java.math.BigDecimal;

public record RedeemResponse(int pointsRedeemed, BigDecimal discountValue, int newBalance) {}

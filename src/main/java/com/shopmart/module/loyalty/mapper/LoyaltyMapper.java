package com.shopmart.module.loyalty.mapper;

import com.shopmart.module.loyalty.dto.LoyaltyTransactionResponse;
import com.shopmart.module.loyalty.entity.LoyaltyTransaction;

public final class LoyaltyMapper {
    private LoyaltyMapper() {}

    public static LoyaltyTransactionResponse toResponse(LoyaltyTransaction t) {
        return new LoyaltyTransactionResponse(t.getId(), t.getPoints(), t.getType().name(),
                t.getOrderId(), t.getDescription(), t.getCreatedAt());
    }
}

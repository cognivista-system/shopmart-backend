package com.shopmart.module.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        String thumbnail,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}

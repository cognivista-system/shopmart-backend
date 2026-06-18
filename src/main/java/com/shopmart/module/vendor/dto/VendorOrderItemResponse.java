package com.shopmart.module.vendor.dto;

import java.math.BigDecimal;

public record VendorOrderItemResponse(
        Long orderItemId,
        Long orderId,
        String orderNumber,
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String orderStatus
) {}

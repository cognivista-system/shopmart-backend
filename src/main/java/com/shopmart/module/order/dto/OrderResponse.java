package com.shopmart.module.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        String paymentStatus,
        String paymentMethod,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal discount,
        BigDecimal total,
        ShippingAddressDto shippingAddress,
        List<OrderItemResponse> items,
        Instant placedAt
) {}

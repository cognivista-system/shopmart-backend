package com.shopmart.module.payment.mapper;

import com.shopmart.module.payment.dto.PaymentResponse;
import com.shopmart.module.payment.entity.Payment;

public final class PaymentMapper {
    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getMethod(), p.getProvider(),
                p.getAmount(), p.getStatus().name(), p.getGatewayRef(), p.getTransactionId(), p.getCreatedAt());
    }
}

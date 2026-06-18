package com.shopmart.module.payment.dto;

import java.math.BigDecimal;

/**
 * Returned when a payment is initiated. For gateway methods, the client uses
 * {@code gatewayRef} (e.g. a Razorpay order id) to launch the checkout widget.
 * For COD, {@code requiresGatewayCheckout} is false and the order is already confirmed.
 */
public record PaymentIntentResponse(
        Long paymentId,
        Long orderId,
        String provider,
        String gatewayRef,
        BigDecimal amount,
        String currency,
        String status,
        boolean requiresGatewayCheckout
) {}

package com.shopmart.module.payment.gateway;

import java.math.BigDecimal;

/**
 * Abstraction over an external payment provider (Razorpay, Stripe, ...).
 * The default {@link StubPaymentGateway} simulates the provider so the flow is
 * end-to-end testable without live credentials. Provide a real implementation
 * (marked @Primary or via @ConditionalOnProperty) in Phase 2.
 */
public interface PaymentGateway {

    String provider();

    /** Create an order/intent on the provider side; returns the provider reference. */
    String createGatewayOrder(Long orderId, BigDecimal amount, String currency);

    /** Verify the signature returned by the provider after checkout. */
    boolean verifySignature(String gatewayRef, String transactionId, String signature);

    /** Refund a captured payment. Returns a gateway refund id, or null if not performed. */
    default String refund(String transactionId, java.math.BigDecimal amount, String currency) {
        return null;
    }
}

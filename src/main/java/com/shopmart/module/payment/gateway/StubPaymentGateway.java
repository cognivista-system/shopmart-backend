package com.shopmart.module.payment.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Development stub. Generates a fake gateway reference and accepts any non-blank
 * signature. DO NOT use in production — replace with a real gateway client that
 * performs HMAC signature verification against your webhook/checkout secret.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.payments.provider", havingValue = "stub", matchIfMissing = true)
public class StubPaymentGateway implements PaymentGateway {

    @Override
    public String provider() {
        return "stub";
    }

    @Override
    public String createGatewayOrder(Long orderId, BigDecimal amount, String currency) {
        String ref = "stub_order_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("[PAYMENT][stub] created gateway order ref={} order={} amount={} {}",
                ref, orderId, amount, currency);
        return ref;
    }

    @Override
    public boolean verifySignature(String gatewayRef, String transactionId, String signature) {
        // Real gateways: recompute HMAC(gatewayRef + "|" + transactionId, secret) and compare.
        boolean ok = signature != null && !signature.isBlank();
        log.info("[PAYMENT][stub] verify ref={} txn={} -> {}", gatewayRef, transactionId, ok);
        return ok;
    }

    @Override
    public String refund(String transactionId, java.math.BigDecimal amount, String currency) {
        String id = "stub_refund_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        log.info("[PAYMENT][stub] refund txn={} amount={} {} -> {}", transactionId, amount, currency, id);
        return id;
    }
}

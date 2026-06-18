package com.shopmart.module.payment.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopmart.common.exception.BadRequestException;
import com.shopmart.util.HmacUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Razorpay Orders API + checkout signature verification.
 * Active when app.payments.provider=razorpay. Uses the JDK HTTP client (no SDK dependency).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.payments.provider", havingValue = "razorpay")
public class RazorpayGateway implements PaymentGateway {

    private static final String ORDERS_URL = "https://api.razorpay.com/v1/orders";

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper objectMapper;
    private final String keyId;
    private final String keySecret;

    public RazorpayGateway(ObjectMapper objectMapper,
                           @Value("${app.payments.razorpay.key-id:}") String keyId,
                           @Value("${app.payments.razorpay.key-secret:}") String keySecret) {
        this.objectMapper = objectMapper;
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    @Override
    public String provider() {
        return "razorpay";
    }

    @Override
    public String createGatewayOrder(Long orderId, BigDecimal amount, String currency) {
        if (keyId.isBlank() || keySecret.isBlank()) {
            throw new BadRequestException("Razorpay credentials are not configured");
        }
        long minor = amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        String body = "{\"amount\":" + minor + ",\"currency\":\"" + currency
                + "\",\"receipt\":\"order_" + orderId + "\"}";
        String auth = Base64.getEncoder()
                .encodeToString((keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(ORDERS_URL))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                log.error("[PAYMENT][razorpay] order create failed status={} body={}", resp.statusCode(), resp.body());
                throw new BadRequestException("Razorpay order creation failed");
            }
            JsonNode node = objectMapper.readTree(resp.body());
            return node.path("id").asText();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("[PAYMENT][razorpay] order create error: {}", e.getMessage());
            throw new BadRequestException("Razorpay order creation error");
        }
    }

    @Override
    public boolean verifySignature(String gatewayRef, String transactionId, String signature) {
        // Razorpay checkout signature = HMAC_SHA256(order_id + "|" + payment_id, key_secret)
        String expected = HmacUtil.hmacSha256Hex(gatewayRef + "|" + transactionId, keySecret);
        boolean ok = HmacUtil.constantTimeEquals(expected, signature);
        log.info("[PAYMENT][razorpay] verify ref={} txn={} -> {}", gatewayRef, transactionId, ok);
        return ok;
    }

    @Override
    public String refund(String transactionId, BigDecimal amount, String currency) {
        if (keySecret.isBlank() || transactionId == null || transactionId.isBlank()) return null;
        long minor = amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
        String auth = Base64.getEncoder()
                .encodeToString((keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));
        try {
            HttpRequest req = HttpRequest.newBuilder(
                            URI.create("https://api.razorpay.com/v1/payments/" + transactionId + "/refund"))
                    .timeout(Duration.ofSeconds(15))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"amount\":" + minor + "}"))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                log.error("[PAYMENT][razorpay] refund failed status={} body={}", resp.statusCode(), resp.body());
                return null;
            }
            return objectMapper.readTree(resp.body()).path("id").asText(null);
        } catch (Exception e) {
            log.error("[PAYMENT][razorpay] refund error: {}", e.getMessage());
            return null;
        }
    }
}

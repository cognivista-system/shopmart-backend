package com.shopmart.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Generic HTTP SMS gateway (MSG91 / Twilio-style). Active when app.sms.enabled=true.
 * Sends a GET to: {api-url}?apikey={key}&sender={senderId}&to={phone}&message={text}
 * Uses the JDK HttpClient — no extra dependencies. Adjust the query layout to your provider.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.sms.enabled", havingValue = "true")
public class HttpSmsSender implements SmsSender {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();

    private final String apiUrl;
    private final String apiKey;
    private final String senderId;

    public HttpSmsSender(@Value("${app.sms.api-url:}") String apiUrl,
                         @Value("${app.sms.api-key:}") String apiKey,
                         @Value("${app.sms.sender-id:SHPMRT}") String senderId) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.senderId = senderId;
    }

    @Override
    public void send(String toPhone, String message) {
        if (toPhone == null || toPhone.isBlank()) return;
        if (apiUrl == null || apiUrl.isBlank()) {
            log.warn("[SMS:http] app.sms.api-url not set; skipping send to={}", toPhone);
            return;
        }
        try {
            String url = apiUrl
                    + (apiUrl.contains("?") ? "&" : "?")
                    + "apikey=" + enc(apiKey)
                    + "&sender=" + enc(senderId)
                    + "&to=" + enc(toPhone)
                    + "&message=" + enc(message);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("[SMS:http] to={} status={}", toPhone, resp.statusCode());
        } catch (Exception e) {
            log.error("[SMS:http] failed to={}: {}", toPhone, e.getMessage());
        }
    }

    private static String enc(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }
}

package com.shopmart.module.search.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Thin Elasticsearch REST helper over the JDK HTTP client (no ES SDK dependency). */
@Slf4j
public class ElasticsearchClient {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public ElasticsearchClient(ObjectMapper objectMapper, String baseUrl) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public JsonNode send(String method, String path, String jsonBody) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json");
        b = switch (method) {
            case "GET" -> b.GET();
            case "DELETE" -> b.DELETE();
            default -> b.method(method, HttpRequest.BodyPublishers.ofString(jsonBody == null ? "" : jsonBody));
        };
        HttpResponse<String> resp = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2 && resp.statusCode() != 404) {
            log.warn("[ES] {} {} -> {} {}", method, path, resp.statusCode(), resp.body());
        }
        return resp.body() == null || resp.body().isBlank()
                ? objectMapper.createObjectNode()
                : objectMapper.readTree(resp.body());
    }

    public ObjectMapper mapper() {
        return objectMapper;
    }
}

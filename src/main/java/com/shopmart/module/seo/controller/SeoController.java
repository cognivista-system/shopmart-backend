package com.shopmart.module.seo.controller;

import com.shopmart.module.seo.service.SeoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "SEO")
public class SeoController {

    private final SeoService service;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        return ResponseEntity.ok(service.sitemapXml());
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        return ResponseEntity.ok(service.robotsTxt());
    }

    @GetMapping(value = "/seo/products/{slug}/structured-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> productJsonLd(@PathVariable String slug) {
        return service.productJsonLd(slug);
    }
}

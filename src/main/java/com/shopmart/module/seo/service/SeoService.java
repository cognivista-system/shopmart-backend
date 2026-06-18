package com.shopmart.module.seo.service;

import java.util.Map;

public interface SeoService {
    String sitemapXml();
    String robotsTxt();
    Map<String, Object> productJsonLd(String slug);
}

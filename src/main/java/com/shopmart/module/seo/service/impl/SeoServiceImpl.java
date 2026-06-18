package com.shopmart.module.seo.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.blog.entity.BlogStatus;
import com.shopmart.module.blog.repository.BlogPostRepository;
import com.shopmart.module.category.repository.CategoryRepository;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.seo.service.SeoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeoServiceImpl implements SeoService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BlogPostRepository blogPostRepository;

    @Value("${app.seo.base-url:https://shopmart.local}")
    private String baseUrl;

    @Override
    @Transactional(readOnly = true)
    public String sitemapXml() {
        String base = baseUrl.replaceAll("/+$", "");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        url(sb, base + "/", "1.0");
        categoryRepository.findAll()
                .forEach(c -> url(sb, base + "/category/" + c.getSlug(), "0.8"));
        productRepository.findByStatus(ProductStatus.ACTIVE, PageRequest.of(0, 5000))
                .forEach(p -> url(sb, base + "/product/" + p.getSlug(), "0.7"));
        blogPostRepository.findByStatus(BlogStatus.PUBLISHED, PageRequest.of(0, 5000))
                .forEach(b -> url(sb, base + "/blog/" + b.getSlug(), "0.5"));
        sb.append("</urlset>\n");
        return sb.toString();
    }

    @Override
    public String robotsTxt() {
        String base = baseUrl.replaceAll("/+$", "");
        return "User-agent: *\n"
                + "Allow: /\n"
                + "Disallow: /api/auth/\n"
                + "Disallow: /api/cart\n"
                + "Disallow: /api/orders\n"
                + "Sitemap: " + base + "/sitemap.xml\n";
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> productJsonLd(String slug) {
        Product p = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));
        String base = baseUrl.replaceAll("/+$", "");
        Map<String, Object> offers = new LinkedHashMap<>();
        offers.put("@type", "Offer");
        offers.put("price", p.getSalePrice() != null ? p.getSalePrice() : p.getPrice());
        offers.put("priceCurrency", "INR");
        offers.put("availability", p.getStatus() == ProductStatus.ACTIVE
                ? "https://schema.org/InStock" : "https://schema.org/OutOfStock");
        offers.put("url", base + "/product/" + p.getSlug());

        Map<String, Object> ld = new LinkedHashMap<>();
        ld.put("@context", "https://schema.org");
        ld.put("@type", "Product");
        ld.put("name", p.getName());
        ld.put("description", p.getDescription());
        ld.put("sku", p.getSku());
        if (p.getBrand() != null) {
            ld.put("brand", Map.of("@type", "Brand", "name", p.getBrand().getName()));
        }
        if (!p.getImages().isEmpty()) {
            ld.put("image", p.getImages().get(0).getUrl());
        }
        if (p.getRatingCount() > 0) {
            ld.put("aggregateRating", Map.of(
                    "@type", "AggregateRating",
                    "ratingValue", p.getRatingAverage(),
                    "reviewCount", p.getRatingCount()));
        }
        ld.put("offers", offers);
        return ld;
    }

    private void url(StringBuilder sb, String loc, String priority) {
        sb.append("  <url><loc>").append(escape(loc)).append("</loc>")
          .append("<priority>").append(priority).append("</priority></url>\n");
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

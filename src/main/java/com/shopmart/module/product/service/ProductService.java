package com.shopmart.module.product.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductFilter;
import com.shopmart.module.product.dto.ProductRequest;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.product.dto.ProductSummary;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    ProductResponse getById(Long id);
    ProductResponse getBySlug(String slug);
    PageResponse<ProductSummary> search(ProductFilter filter, Pageable pageable);
    List<ProductSummary> featured(int limit);
    void updateStock(Long id, int stock);

    // ---- Vendor self-service (products require admin approval) ----
    ProductResponse createForVendor(Long vendorId, ProductRequest request);
    ProductResponse updateForVendor(Long vendorId, Long productId, ProductRequest request);

    // ---- Admin approval workflow ----
    PageResponse<ProductSummary> pendingApproval(Pageable pageable);
    ProductResponse approve(Long id);
    ProductResponse reject(Long id, String reason);
}

package com.shopmart.module.vendor.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductRequest;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.vendor.dto.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface VendorService {
    // vendor self-service
    VendorResponse register(Long userId, VendorRegistrationRequest request);
    VendorResponse getMyVendor(Long userId);
    VendorResponse updateProfile(Long userId, VendorUpdateRequest request);
    PageResponse<ProductSummary> myProducts(Long userId, Pageable pageable);
    PageResponse<VendorOrderItemResponse> myOrders(Long userId, Pageable pageable);
    VendorEarningsResponse myEarnings(Long userId);
    PageResponse<PayoutResponse> myPayouts(Long userId, Pageable pageable);
    VendorDashboardResponse myDashboard(Long userId);

    // vendor product submission (require admin approval)
    ProductResponse createProduct(Long userId, ProductRequest request);
    ProductResponse updateProduct(Long userId, Long productId, ProductRequest request);

    // public
    VendorResponse getBySlug(String slug);

    // admin
    VendorResponse getById(Long id);
    PageResponse<VendorResponse> list(String status, Pageable pageable);
    VendorResponse updateStatus(Long id, VendorStatusUpdateRequest request);
    VendorResponse updateCommission(Long id, BigDecimal commissionRate);
    PayoutResponse createPayout(Long vendorId, PayoutRequest request);
    PayoutResponse markPayoutPaid(Long payoutId);
    PageResponse<PayoutResponse> listPayouts(Long vendorId, Pageable pageable);
}

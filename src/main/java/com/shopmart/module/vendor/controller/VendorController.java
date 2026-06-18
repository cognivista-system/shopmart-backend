package com.shopmart.module.vendor.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.dto.ProductRequest;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.vendor.dto.*;
import com.shopmart.module.vendor.service.VendorService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendors")
public class VendorController {

    private final VendorService service;

    // ---- Registration (any authenticated user) ----

    @PostMapping("/register")
    public ApiResponse<VendorResponse> register(@Valid @RequestBody VendorRegistrationRequest request) {
        return ApiResponse.ok("Vendor application submitted",
                service.register(SecurityUtils.currentUserId(), request));
    }

    // ---- Vendor self-service ----

    @GetMapping("/me")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<VendorResponse> me() {
        return ApiResponse.ok(service.getMyVendor(SecurityUtils.currentUserId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<VendorResponse> updateProfile(@Valid @RequestBody VendorUpdateRequest request) {
        return ApiResponse.ok("Profile updated", service.updateProfile(SecurityUtils.currentUserId(), request));
    }

    @GetMapping("/me/products")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<PageResponse<ProductSummary>> myProducts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.myProducts(SecurityUtils.currentUserId(), pageable));
    }

    @GetMapping("/me/orders")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<PageResponse<VendorOrderItemResponse>> myOrders(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ApiResponse.ok(service.myOrders(SecurityUtils.currentUserId(), pageable));
    }

    @GetMapping("/me/earnings")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<VendorEarningsResponse> myEarnings() {
        return ApiResponse.ok(service.myEarnings(SecurityUtils.currentUserId()));
    }

    @GetMapping("/me/payouts")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<PageResponse<PayoutResponse>> myPayouts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.myPayouts(SecurityUtils.currentUserId(), pageable));
    }

    @GetMapping("/me/dashboard")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<VendorDashboardResponse> myDashboard() {
        return ApiResponse.ok(service.myDashboard(SecurityUtils.currentUserId()));
    }

    @PostMapping("/me/products")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok("Product submitted for approval",
                service.createProduct(SecurityUtils.currentUserId(), request));
    }

    @PutMapping("/me/products/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @Valid @RequestBody ProductRequest request) {
        return ApiResponse.ok("Product updated and resubmitted for approval",
                service.updateProduct(SecurityUtils.currentUserId(), id, request));
    }

    // ---- Public storefront ----

    @GetMapping("/store/{slug}")
    public ApiResponse<VendorResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok(service.getBySlug(slug));
    }

    // ---- Admin ----

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<VendorResponse>> list(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.list(status, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VendorResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VendorResponse> updateStatus(@PathVariable Long id,
                                                    @Valid @RequestBody VendorStatusUpdateRequest request) {
        return ApiResponse.ok("Vendor status updated", service.updateStatus(id, request));
    }

    @PatchMapping("/{id}/commission")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VendorResponse> updateCommission(@PathVariable Long id,
                                                        @Valid @RequestBody CommissionUpdateRequest request) {
        return ApiResponse.ok("Commission rate updated",
                service.updateCommission(id, request.commissionRate()));
    }

    @PostMapping("/{id}/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PayoutResponse> createPayout(@PathVariable Long id,
                                                    @Valid @RequestBody PayoutRequest request) {
        return ApiResponse.ok("Payout recorded", service.createPayout(id, request));
    }

    @GetMapping("/{id}/payouts")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<PayoutResponse>> listPayouts(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.listPayouts(id, pageable));
    }

    @PatchMapping("/payouts/{payoutId}/paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PayoutResponse> markPayoutPaid(@PathVariable Long payoutId) {
        return ApiResponse.ok("Payout marked paid", service.markPayoutPaid(payoutId));
    }
}

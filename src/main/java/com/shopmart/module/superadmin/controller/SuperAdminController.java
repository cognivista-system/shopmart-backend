package com.shopmart.module.superadmin.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.dto.RejectRequest;
import com.shopmart.module.product.service.ProductService;
import com.shopmart.module.superadmin.dto.AdminCreatedResponse;
import com.shopmart.module.superadmin.dto.CreateAdminRequest;
import com.shopmart.module.superadmin.dto.SuperAdminDashboardResponse;
import com.shopmart.module.superadmin.service.SuperAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Super Admin endpoints. All routes are restricted to ROLE_SUPER_ADMIN
 * (also enforced at the path level in SecurityConfig: /super-admin/**).
 */
@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final ProductService productService;

    // ---- Product approval ----

    @GetMapping("/products/pending")
    public ApiResponse<PageResponse<ProductSummary>> pendingProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok(productService.pendingApproval(pageable));
    }

    @PutMapping("/products/{id}/approve")
    public ApiResponse<ProductResponse> approve(@PathVariable Long id) {
        return ApiResponse.ok("Product approved", productService.approve(id));
    }

    @PutMapping("/products/{id}/reject")
    public ApiResponse<ProductResponse> reject(@PathVariable Long id,
                                               @RequestBody RejectRequest request) {
        return ApiResponse.ok("Product rejected", productService.reject(id, request.reason()));
    }

    // ---- Admin management ----

    @PostMapping("/admin/create")
    public ApiResponse<AdminCreatedResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ApiResponse.ok("Admin created", superAdminService.createAdmin(request));
    }

    // ---- Dashboard ----

    @GetMapping("/dashboard")
    public ApiResponse<SuperAdminDashboardResponse> dashboard() {
        return ApiResponse.ok(superAdminService.dashboard());
    }
}

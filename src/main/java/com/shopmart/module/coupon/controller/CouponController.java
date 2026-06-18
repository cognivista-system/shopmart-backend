package com.shopmart.module.coupon.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.coupon.dto.CouponApplication;
import com.shopmart.module.coupon.dto.CouponRequest;
import com.shopmart.module.coupon.dto.CouponResponse;
import com.shopmart.module.coupon.dto.ValidateCouponRequest;
import com.shopmart.module.coupon.service.CouponService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons")
public class CouponController {

    private final CouponService service;

    // ---- Customer ----

    @PostMapping("/validate")
    public ApiResponse<CouponApplication> validate(@Valid @RequestBody ValidateCouponRequest request) {
        CouponApplication result = service.validate(
                request.code(), SecurityUtils.currentUserId(), request.orderAmount());
        return ApiResponse.ok("Coupon applied", result);
    }

    // ---- Admin ----

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<CouponResponse>> getAll() {
        return ApiResponse.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> create(@Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Coupon created", service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CouponResponse> update(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Coupon updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Coupon deleted");
    }
}

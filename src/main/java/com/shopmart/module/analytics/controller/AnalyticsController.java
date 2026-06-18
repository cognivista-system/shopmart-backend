package com.shopmart.module.analytics.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.analytics.dto.*;
import com.shopmart.module.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> dashboard() {
        return ApiResponse.ok(service.dashboard());
    }

    @GetMapping("/sales")
    public ApiResponse<List<SalesPoint>> sales(@RequestParam(defaultValue = "30") int days) {
        return ApiResponse.ok(service.salesOverTime(days));
    }

    @GetMapping("/top-products")
    public ApiResponse<List<TopProduct>> topProducts(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.ok(service.topProducts(limit));
    }

    @GetMapping("/order-status")
    public ApiResponse<List<StatusCount>> orderStatus() {
        return ApiResponse.ok(service.orderStatusBreakdown());
    }

    @GetMapping("/low-stock")
    public ApiResponse<List<LowStockProduct>> lowStock(@RequestParam(defaultValue = "5") int threshold) {
        return ApiResponse.ok(service.lowStock(threshold));
    }

    @GetMapping("/customers")
    public ApiResponse<CustomerAnalyticsResponse> customers() {
        return ApiResponse.ok(service.customerAnalytics());
    }
}

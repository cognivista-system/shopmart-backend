package com.shopmart.module.admin.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.admin.dto.AdminDashboardResponse;
import com.shopmart.module.admin.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard")
public class AdminDashboardController {

    private final AdminDashboardService service;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ApiResponse<AdminDashboardResponse> dashboard() {
        return ApiResponse.ok(service.dashboard());
    }
}

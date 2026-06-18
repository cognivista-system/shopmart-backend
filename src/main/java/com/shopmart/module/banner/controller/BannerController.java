package com.shopmart.module.banner.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.banner.dto.BannerRequest;
import com.shopmart.module.banner.dto.BannerResponse;
import com.shopmart.module.banner.service.BannerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@Tag(name = "Banners")
public class BannerController {

    private final BannerService service;

    // ---- Public ----
    @GetMapping
    public ApiResponse<List<BannerResponse>> active() {
        return ApiResponse.ok(service.listActive());
    }

    // ---- Admin ----
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<BannerResponse>> all() {
        return ApiResponse.ok(service.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> create(@Valid @RequestBody BannerRequest request) {
        return ApiResponse.ok("Banner created", service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BannerResponse> update(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        return ApiResponse.ok("Banner updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Banner deleted");
    }
}

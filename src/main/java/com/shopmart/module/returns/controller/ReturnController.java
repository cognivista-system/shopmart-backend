package com.shopmart.module.returns.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.returns.dto.CreateReturnRequest;
import com.shopmart.module.returns.dto.ReturnDecisionRequest;
import com.shopmart.module.returns.dto.ReturnResponse;
import com.shopmart.module.returns.service.ReturnService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/returns")
@RequiredArgsConstructor
@Tag(name = "Returns & Refunds")
public class ReturnController {

    private final ReturnService service;

    // ---- Customer ----
    @PostMapping
    public ApiResponse<ReturnResponse> create(@Valid @RequestBody CreateReturnRequest request) {
        return ApiResponse.ok("Return requested", service.create(SecurityUtils.currentUserId(), request));
    }

    @GetMapping
    public ApiResponse<List<ReturnResponse>> mine() {
        return ApiResponse.ok(service.listMine(SecurityUtils.currentUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ReturnResponse> getMine(@PathVariable Long id) {
        return ApiResponse.ok(service.getMine(SecurityUtils.currentUserId(), id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<ReturnResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok("Return cancelled", service.cancelMine(SecurityUtils.currentUserId(), id));
    }

    // ---- Admin ----
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ReturnResponse>> all(@RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listAll(status));
    }

    @PostMapping("/admin/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReturnResponse> decide(@PathVariable Long id,
                                              @Valid @RequestBody ReturnDecisionRequest request) {
        return ApiResponse.ok("Return processed", service.decide(id, request));
    }
}

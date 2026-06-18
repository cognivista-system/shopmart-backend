package com.shopmart.module.mobile.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.mobile.dto.DeviceRegisterRequest;
import com.shopmart.module.mobile.dto.MobileConfigResponse;
import com.shopmart.module.mobile.service.MobileService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mobile")
@RequiredArgsConstructor
@Tag(name = "Mobile")
public class MobileController {

    private final MobileService service;

    @GetMapping("/config")
    public ApiResponse<MobileConfigResponse> config() {
        return ApiResponse.ok(service.config());
    }

    @PostMapping("/devices")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody DeviceRegisterRequest request) {
        Long id = service.registerDevice(SecurityUtils.currentUserId(), request);
        return ApiResponse.ok("Device registered", Map.of("id", id));
    }

    @DeleteMapping("/devices/{token}")
    public ApiResponse<Void> unregister(@PathVariable String token) {
        service.unregisterDevice(token);
        return ApiResponse.message("Device unregistered");
    }
}

package com.shopmart.module.attendance.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.attendance.dto.AttendanceResponse;
import com.shopmart.module.attendance.service.AttendanceService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance")
public class AttendanceController {

    private final AttendanceService service;

    @PostMapping("/login")
    public ApiResponse<AttendanceResponse> login() {
        return ApiResponse.ok("Session started", service.login(SecurityUtils.currentUserId()));
    }

    @PostMapping("/logout")
    public ApiResponse<AttendanceResponse> logout() {
        return ApiResponse.ok("Session ended", service.logout(SecurityUtils.currentUserId()));
    }

    @GetMapping("/my-hours")
    public ApiResponse<List<AttendanceResponse>> myHours() {
        return ApiResponse.ok(service.myHours(SecurityUtils.currentUserId()));
    }
}

package com.shopmart.module.auth.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.auth.dto.*;
import com.shopmart.module.auth.service.AuthService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registered. Please verify the OTP sent to your email.", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive access + refresh tokens")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }

    @PostMapping("/google")
    @Operation(summary = "Sign in or sign up with a Google ID token (used by login & register pages)")
    public ApiResponse<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request) {
        return ApiResponse.ok("Login successful", authService.googleLogin(request.idToken()));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Exchange a refresh token for a new token pair")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke all refresh tokens for the current user")
    public ApiResponse<Void> logout() {
        authService.logout(SecurityUtils.currentUserId());
        return ApiResponse.message("Logged out");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify the email-verification OTP")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody OtpRequest request) {
        authService.verifyOtp(request);
        return ApiResponse.message("Email verified");
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Resend the email-verification OTP")
    public ApiResponse<Void> resendOtp(@Valid @RequestBody EmailRequest request) {
        authService.resendOtp(request.email());
        return ApiResponse.message("OTP resent");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send a password-reset OTP")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody EmailRequest request) {
        authService.forgotPassword(request.email());
        return ApiResponse.message("If that email exists, a reset code has been sent");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using an OTP")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.message("Password reset successful");
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current user's profile")
    public ApiResponse<UserResponse> getProfile() {
        return ApiResponse.ok(authService.getProfile(SecurityUtils.currentUserId()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update the current user's profile")
    public ApiResponse<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated",
                authService.updateProfile(SecurityUtils.currentUserId(), request));
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change the current user's password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(SecurityUtils.currentUserId(), request);
        return ApiResponse.message("Password changed");
    }
}

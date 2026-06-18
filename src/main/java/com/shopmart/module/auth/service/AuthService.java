package com.shopmart.module.auth.service;

import com.shopmart.module.auth.dto.*;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse googleLogin(String idToken);
    AuthResponse refreshToken(String refreshToken);
    void logout(Long userId);
    void verifyOtp(OtpRequest request);
    void resendOtp(String email);
    void forgotPassword(String email);
    void resetPassword(ResetPasswordRequest request);
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}

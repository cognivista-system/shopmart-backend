package com.shopmart.module.auth.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.common.notification.NotificationService;
import com.shopmart.module.auth.dto.*;
import com.shopmart.module.auth.entity.OtpVerification;
import com.shopmart.module.auth.entity.OtpVerification.OtpPurpose;
import com.shopmart.module.auth.entity.RefreshToken;
import com.shopmart.module.auth.mapper.UserMapper;
import com.shopmart.module.auth.repository.OtpVerificationRepository;
import com.shopmart.module.auth.repository.RefreshTokenRepository;
import com.shopmart.module.auth.service.AuthService;
import com.shopmart.module.auth.service.GoogleTokenVerifier;
import com.shopmart.module.auth.service.GoogleUserInfo;
import com.shopmart.module.user.entity.AuthProvider;
import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import com.shopmart.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final NotificationService notificationService;
    private final GoogleTokenVerifier googleTokenVerifier;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long OTP_TTL_MINUTES = 10;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.addRole(Role.ROLE_CUSTOMER);
        user = userRepository.save(user);

        issueOtp(user.getEmail(), OtpPurpose.EMAIL_VERIFICATION);
        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }
        if (!user.isEnabled()) {
            throw new BadRequestException("This account has been disabled");
        }
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(String idToken) {
        GoogleUserInfo info = googleTokenVerifier.verify(idToken);
        if (info.email() == null || info.email().isBlank()) {
            throw new BadRequestException("Google account has no email");
        }
        String email = info.email().toLowerCase();

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setName(info.name() != null ? info.name() : email);
            u.setEmail(email);
            u.setAvatarUrl(info.picture());
            u.setProvider(AuthProvider.GOOGLE);
            u.setProviderId(info.sub());
            // Google verifies the email; no local password is usable for this account.
            u.setEmailVerified(info.emailVerified());
            u.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
            u.addRole(Role.ROLE_CUSTOMER);
            return userRepository.save(u);
        });

        if (!user.isEnabled()) {
            throw new BadRequestException("This account has been disabled");
        }
        // Link the Google identity to a pre-existing account on first Google sign-in.
        if (user.getProviderId() == null) {
            user.setProviderId(info.sub());
            userRepository.save(user);
        }
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.isValid(refreshToken) || !"refresh".equals(tokenProvider.getTokenType(refreshToken))) {
            throw new BadRequestException("Invalid refresh token");
        }
        RefreshToken stored = refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new BadRequestException("Refresh token not recognised or already used"));
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token has expired");
        }
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", stored.getUserId()));
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(user);
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }

    @Override
    @Transactional
    public void verifyOtp(OtpRequest request) {
        OtpVerification otp = consumeOtp(request.email(), request.code(), OtpPurpose.EMAIL_VERIFICATION);
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        user.setEmailVerified(true);
        userRepository.save(user);
        otp.setUsed(true);
        otpRepository.save(otp);
    }

    @Override
    @Transactional
    public void resendOtp(String email) {
        userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        issueOtp(email.toLowerCase(), OtpPurpose.EMAIL_VERIFICATION);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // Do not reveal whether the email exists.
        userRepository.findByEmail(email.toLowerCase())
                .ifPresent(u -> issueOtp(u.getEmail(), OtpPurpose.PASSWORD_RESET));
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        OtpVerification otp = consumeOtp(request.email(), request.code(), OtpPurpose.PASSWORD_RESET);
        User user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        otp.setUsed(true);
        otpRepository.save(otp);
        refreshTokenRepository.revokeAllForUser(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserMapper.toResponse(findUser(userId));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        if (request.name() != null) user.setName(request.name());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.avatarUrl() != null) user.setAvatarUrl(request.avatarUrl());
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUser(userId);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        refreshTokenRepository.revokeAllForUser(userId);
    }

    // ---- helpers ----

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private AuthResponse issueTokens(User user) {
        String access = tokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refresh = tokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        RefreshToken token = new RefreshToken();
        token.setUserId(user.getId());
        token.setToken(refresh);
        token.setExpiresAt(Instant.now().plusMillis(tokenProvider.getRefreshExpMs()));
        refreshTokenRepository.save(token);

        return AuthResponse.of(access, refresh, UserMapper.toResponse(user));
    }

    private void issueOtp(String email, OtpPurpose purpose) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        OtpVerification otp = new OtpVerification();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setPurpose(purpose);
        otp.setExpiresAt(Instant.now().plus(OTP_TTL_MINUTES, ChronoUnit.MINUTES));
        otpRepository.save(otp);
        String phone = userRepository.findByEmail(email).map(User::getPhone).orElse(null);
        notificationService.sendOtp(email, phone, code, purpose.name());
    }

    private OtpVerification consumeOtp(String email, String code, OtpPurpose purpose) {
        OtpVerification otp = otpRepository
                .findFirstByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email.toLowerCase(), purpose)
                .orElseThrow(() -> new BadRequestException("No pending verification code found"));
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Verification code has expired");
        }
        if (!otp.getCode().equals(code)) {
            throw new BadRequestException("Incorrect verification code");
        }
        return otp;
    }
}

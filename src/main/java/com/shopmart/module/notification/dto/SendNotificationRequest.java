package com.shopmart.module.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record SendNotificationRequest(
        Long userId,            // null => broadcast to all users (admin only)
        String type,            // ORDER | PAYMENT | PROMO | REVIEW | SYSTEM
        @NotBlank String title,
        String message,
        String link
) {}

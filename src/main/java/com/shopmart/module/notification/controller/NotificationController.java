package com.shopmart.module.notification.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.notification.dto.NotificationResponse;
import com.shopmart.module.notification.dto.SendNotificationRequest;
import com.shopmart.module.notification.entity.NotificationType;
import com.shopmart.module.notification.service.UserNotificationService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final UserNotificationService service;

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> list(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.list(SecurityUtils.currentUserId(), pageable));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount() {
        return ApiResponse.ok(Map.of("unread", service.unreadCount(SecurityUtils.currentUserId())));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id) {
        service.markRead(SecurityUtils.currentUserId(), id);
        return ApiResponse.message("Marked as read");
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        service.markAllRead(SecurityUtils.currentUserId());
        return ApiResponse.message("All notifications marked as read");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(SecurityUtils.currentUserId(), id);
        return ApiResponse.message("Notification deleted");
    }

    // ---- Admin ----

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> send(@Valid @RequestBody SendNotificationRequest request) {
        NotificationType type = parseType(request.type());
        if (request.userId() == null) {
            int count = service.broadcast(type, request.title(), request.message(), request.link());
            return ApiResponse.ok("Broadcast sent", Map.<String, Object>of("recipients", count));
        }
        service.sendToUser(request.userId(), type, request.title(), request.message(), request.link());
        return ApiResponse.ok("Notification sent", Map.<String, Object>of("recipients", 1));
    }

    private NotificationType parseType(String type) {
        if (type == null) return NotificationType.SYSTEM;
        try {
            return NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NotificationType.SYSTEM;
        }
    }
}

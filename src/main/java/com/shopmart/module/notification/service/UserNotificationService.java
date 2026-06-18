package com.shopmart.module.notification.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.notification.dto.NotificationResponse;
import com.shopmart.module.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;

public interface UserNotificationService {

    /** Internal helper for other modules to raise an in-app notification. */
    void notify(Long userId, NotificationType type, String title, String message, String link);

    PageResponse<NotificationResponse> list(Long userId, Pageable pageable);
    long unreadCount(Long userId);
    void markRead(Long userId, Long notificationId);
    void markAllRead(Long userId);
    void delete(Long userId, Long notificationId);

    /** Admin: send to one user. */
    void sendToUser(Long userId, NotificationType type, String title, String message, String link);

    /** Admin: send to every user. */
    int broadcast(NotificationType type, String title, String message, String link);
}

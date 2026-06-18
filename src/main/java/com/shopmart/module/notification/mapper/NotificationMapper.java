package com.shopmart.module.notification.mapper;

import com.shopmart.module.notification.dto.NotificationResponse;
import com.shopmart.module.notification.entity.Notification;

public final class NotificationMapper {
    private NotificationMapper() {}

    public static NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getType().name(), n.getTitle(),
                n.getMessage(), n.getLink(), n.isRead(), n.getCreatedAt());
    }
}

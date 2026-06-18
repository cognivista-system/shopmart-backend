package com.shopmart.module.notification.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.notification.dto.NotificationResponse;
import com.shopmart.module.notification.entity.Notification;
import com.shopmart.module.notification.entity.NotificationType;
import com.shopmart.module.notification.mapper.NotificationMapper;
import com.shopmart.module.notification.repository.NotificationRepository;
import com.shopmart.module.notification.service.UserNotificationService;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void notify(Long userId, NotificationType type, String title, String message, String link) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setLink(link);
        repository.save(n);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Long userId, Pageable pageable) {
        Page<NotificationResponse> page = repository.findByUserId(userId, pageable)
                .map(NotificationMapper::toResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return repository.countByUserIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification n = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        n.setRead(true);
        repository.save(n);
    }

    @Override
    @Transactional
    public void markAllRead(Long userId) {
        repository.markAllRead(userId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long notificationId) {
        Notification n = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        repository.delete(n);
    }

    @Override
    @Transactional
    public void sendToUser(Long userId, NotificationType type, String title, String message, String link) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        notify(userId, type, title, message, link);
    }

    @Override
    @Transactional
    public int broadcast(NotificationType type, String title, String message, String link) {
        List<User> users = userRepository.findAll();
        for (User u : users) {
            notify(u.getId(), type, title, message, link);
        }
        return users.size();
    }
}

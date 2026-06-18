package com.shopmart.common.notification;

public interface EmailSender {
    void send(String to, String subject, String body);
}

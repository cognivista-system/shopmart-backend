package com.shopmart.common.notification;

public interface SmsSender {
    void send(String toPhone, String message);
}

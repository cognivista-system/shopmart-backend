package com.shopmart.common.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Routes transactional notifications to the configured channels.
 * OTP -> email (if address present) and SMS (if phone present).
 * Order confirmation -> email.
 * The actual channel beans (Email/SmsSender) are swapped via app.mail.enabled / app.sms.enabled.
 */
@Service
@RequiredArgsConstructor
public class DefaultNotificationService implements NotificationService {

    private final EmailSender emailSender;
    private final SmsSender smsSender;

    @Override
    public void sendOtp(String email, String phone, String code, String purpose) {
        String subject = "Your ShopMart verification code";
        String body = "Your ShopMart " + purpose.toLowerCase().replace('_', ' ')
                + " code is " + code + ". It expires shortly. Do not share it with anyone.";
        if (email != null && !email.isBlank()) {
            emailSender.send(email, subject, body);
        }
        if (phone != null && !phone.isBlank()) {
            smsSender.send(phone, "ShopMart code: " + code);
        }
    }

    @Override
    public void sendOrderConfirmation(String email, String orderNumber) {
        emailSender.send(email,
                "Your ShopMart order " + orderNumber,
                "Thank you! Your order " + orderNumber + " has been received and is being processed.");
    }
}

package com.shopmart.common.notification;

public interface NotificationService {
    /** Sends the OTP via email and/or SMS depending on which contacts are present and which channels are enabled. */
    void sendOtp(String email, String phone, String code, String purpose);

    void sendOrderConfirmation(String email, String orderNumber);
}

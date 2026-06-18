package com.shopmart.module.payment.service;

import com.shopmart.module.payment.dto.CreatePaymentRequest;
import com.shopmart.module.payment.dto.PaymentIntentResponse;
import com.shopmart.module.payment.dto.PaymentResponse;
import com.shopmart.module.payment.dto.VerifyPaymentRequest;

import java.util.List;

public interface PaymentService {
    PaymentIntentResponse initiate(Long userId, CreatePaymentRequest request);
    PaymentResponse verify(Long userId, VerifyPaymentRequest request);
    List<PaymentResponse> history(Long userId, Long orderId);
}

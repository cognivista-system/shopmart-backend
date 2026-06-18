package com.shopmart.module.payment.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.payment.dto.CreatePaymentRequest;
import com.shopmart.module.payment.dto.PaymentIntentResponse;
import com.shopmart.module.payment.dto.PaymentResponse;
import com.shopmart.module.payment.dto.VerifyPaymentRequest;
import com.shopmart.module.payment.service.PaymentService;
import com.shopmart.security.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/initiate")
    public ApiResponse<PaymentIntentResponse> initiate(@Valid @RequestBody CreatePaymentRequest request) {
        return ApiResponse.ok("Payment initiated", service.initiate(SecurityUtils.currentUserId(), request));
    }

    @PostMapping("/verify")
    public ApiResponse<PaymentResponse> verify(@Valid @RequestBody VerifyPaymentRequest request) {
        return ApiResponse.ok("Payment verified", service.verify(SecurityUtils.currentUserId(), request));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<PaymentResponse>> history(@PathVariable Long orderId) {
        return ApiResponse.ok(service.history(SecurityUtils.currentUserId(), orderId));
    }
}

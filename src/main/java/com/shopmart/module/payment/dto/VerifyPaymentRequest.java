package com.shopmart.module.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyPaymentRequest(
        @NotNull Long orderId,
        @NotBlank String gatewayRef,
        @NotBlank String transactionId,
        @NotBlank String signature
) {}

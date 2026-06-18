package com.shopmart.module.payment.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(@NotNull Long orderId) {}

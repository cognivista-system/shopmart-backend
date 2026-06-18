package com.shopmart.module.returns.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Admin decision. approve=true -> refund is processed; refundAmount optional (defaults to order total). */
public record ReturnDecisionRequest(
        @NotNull Boolean approve,
        String adminNote,
        BigDecimal refundAmount
) {}

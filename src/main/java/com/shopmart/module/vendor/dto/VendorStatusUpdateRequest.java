package com.shopmart.module.vendor.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record VendorStatusUpdateRequest(
        @NotBlank String status,          // PENDING | APPROVED | SUSPENDED | REJECTED
        BigDecimal commissionRate         // optional override
) {}

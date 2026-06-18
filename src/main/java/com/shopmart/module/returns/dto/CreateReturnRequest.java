package com.shopmart.module.returns.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReturnRequest(
        @NotNull Long orderId,
        @NotBlank String reason
) {}

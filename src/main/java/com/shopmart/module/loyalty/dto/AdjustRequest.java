package com.shopmart.module.loyalty.dto;

import jakarta.validation.constraints.NotNull;

public record AdjustRequest(@NotNull Long userId, @NotNull Integer points, String reason) {}

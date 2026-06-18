package com.shopmart.module.loyalty.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RedeemRequest(@NotNull @Min(1) Integer points) {}

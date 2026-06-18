package com.shopmart.module.warehouse.dto;

import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(
        @NotNull Long productId,
        @NotNull Integer quantity,
        String mode            // SET (default) | ADJUST (delta)
) {}

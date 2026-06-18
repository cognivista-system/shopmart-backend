package com.shopmart.module.warehouse.dto;

import java.time.Instant;

public record StockTransferResponse(
        Long id,
        Long fromWarehouseId,
        Long toWarehouseId,
        Long productId,
        int quantity,
        String status,
        String note,
        Instant createdAt
) {}

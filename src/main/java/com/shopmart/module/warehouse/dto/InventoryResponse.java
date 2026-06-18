package com.shopmart.module.warehouse.dto;

public record InventoryResponse(
        Long warehouseId,
        String warehouseName,
        Long productId,
        int quantity,
        int reserved,
        int available
) {}

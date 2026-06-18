package com.shopmart.module.warehouse.dto;

import java.util.List;

public record ProductStockResponse(
        Long productId,
        int totalQuantity,
        List<InventoryResponse> byWarehouse
) {}

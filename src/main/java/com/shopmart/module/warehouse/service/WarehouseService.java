package com.shopmart.module.warehouse.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.warehouse.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse update(Long id, WarehouseRequest request);
    void delete(Long id);
    WarehouseResponse getById(Long id);
    List<WarehouseResponse> getAll();

    List<InventoryResponse> getInventory(Long warehouseId);
    InventoryResponse updateInventory(Long warehouseId, InventoryUpdateRequest request);
    ProductStockResponse getProductStock(Long productId);

    StockTransferResponse transfer(StockTransferRequest request);
    PageResponse<StockTransferResponse> listTransfers(Long productId, Pageable pageable);

    List<InventoryResponse> lowStock(Long warehouseId, int threshold);
}

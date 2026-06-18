package com.shopmart.module.warehouse.mapper;

import com.shopmart.module.warehouse.dto.StockTransferResponse;
import com.shopmart.module.warehouse.dto.WarehouseResponse;
import com.shopmart.module.warehouse.entity.StockTransfer;
import com.shopmart.module.warehouse.entity.Warehouse;

public final class WarehouseMapper {
    private WarehouseMapper() {}

    public static WarehouseResponse toResponse(Warehouse w) {
        return new WarehouseResponse(w.getId(), w.getName(), w.getCode(), w.getAddressLine(),
                w.getCity(), w.getState(), w.getCountry(), w.getPostalCode(), w.isActive());
    }

    public static StockTransferResponse toResponse(StockTransfer t) {
        return new StockTransferResponse(t.getId(), t.getFromWarehouseId(), t.getToWarehouseId(),
                t.getProductId(), t.getQuantity(), t.getStatus().name(), t.getNote(), t.getCreatedAt());
    }
}

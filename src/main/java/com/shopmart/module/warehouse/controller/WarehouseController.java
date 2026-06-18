package com.shopmart.module.warehouse.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.warehouse.dto.*;
import com.shopmart.module.warehouse.service.WarehouseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Warehouses")
public class WarehouseController {

    private final WarehouseService service;

    @GetMapping
    public ApiResponse<List<WarehouseResponse>> getAll() {
        return ApiResponse.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<WarehouseResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(service.getById(id));
    }

    @PostMapping
    public ApiResponse<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
        return ApiResponse.ok("Warehouse created", service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<WarehouseResponse> update(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return ApiResponse.ok("Warehouse updated", service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.message("Warehouse deleted");
    }

    // ---- Inventory ----

    @GetMapping("/{id}/inventory")
    public ApiResponse<List<InventoryResponse>> getInventory(@PathVariable Long id) {
        return ApiResponse.ok(service.getInventory(id));
    }

    @PutMapping("/{id}/inventory")
    public ApiResponse<InventoryResponse> updateInventory(@PathVariable Long id,
                                                          @Valid @RequestBody InventoryUpdateRequest request) {
        return ApiResponse.ok("Inventory updated", service.updateInventory(id, request));
    }

    @GetMapping("/inventory/product/{productId}")
    public ApiResponse<ProductStockResponse> getProductStock(@PathVariable Long productId) {
        return ApiResponse.ok(service.getProductStock(productId));
    }

    @GetMapping("/{id}/low-stock")
    public ApiResponse<List<InventoryResponse>> lowStock(@PathVariable Long id,
                                                         @RequestParam(defaultValue = "5") int threshold) {
        return ApiResponse.ok(service.lowStock(id, threshold));
    }

    // ---- Transfers ----

    @PostMapping("/transfers")
    public ApiResponse<StockTransferResponse> transfer(@Valid @RequestBody StockTransferRequest request) {
        return ApiResponse.ok("Stock transferred", service.transfer(request));
    }

    @GetMapping("/transfers")
    public ApiResponse<PageResponse<StockTransferResponse>> listTransfers(
            @RequestParam Long productId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ApiResponse.ok(service.listTransfers(productId, pageable));
    }
}

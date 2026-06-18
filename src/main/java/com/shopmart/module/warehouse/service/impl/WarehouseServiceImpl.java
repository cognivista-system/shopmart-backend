package com.shopmart.module.warehouse.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.warehouse.dto.*;
import com.shopmart.module.warehouse.entity.StockTransfer;
import com.shopmart.module.warehouse.entity.TransferStatus;
import com.shopmart.module.warehouse.entity.Warehouse;
import com.shopmart.module.warehouse.entity.WarehouseInventory;
import com.shopmart.module.warehouse.mapper.WarehouseMapper;
import com.shopmart.module.warehouse.repository.StockTransferRepository;
import com.shopmart.module.warehouse.repository.WarehouseInventoryRepository;
import com.shopmart.module.warehouse.repository.WarehouseRepository;
import com.shopmart.module.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository inventoryRepository;
    private final StockTransferRepository transferRepository;

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepository.existsByCode(request.code())) {
            throw new ConflictException("A warehouse with this code already exists");
        }
        Warehouse w = new Warehouse();
        apply(w, request);
        return WarehouseMapper.toResponse(warehouseRepository.save(w));
    }

    @Override
    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = findWarehouse(id);
        if (!w.getCode().equals(request.code()) && warehouseRepository.existsByCode(request.code())) {
            throw new ConflictException("A warehouse with this code already exists");
        }
        apply(w, request);
        return WarehouseMapper.toResponse(warehouseRepository.save(w));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        warehouseRepository.delete(findWarehouse(id));
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getById(Long id) {
        return WarehouseMapper.toResponse(findWarehouse(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findAll().stream().map(WarehouseMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory(Long warehouseId) {
        Warehouse w = findWarehouse(warehouseId);
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(i -> toInventoryResponse(i, w.getName()))
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long warehouseId, InventoryUpdateRequest request) {
        Warehouse w = findWarehouse(warehouseId);
        WarehouseInventory inv = inventoryRepository
                .findByWarehouseIdAndProductId(warehouseId, request.productId())
                .orElseGet(() -> {
                    WarehouseInventory created = new WarehouseInventory();
                    created.setWarehouseId(warehouseId);
                    created.setProductId(request.productId());
                    return created;
                });

        boolean adjust = request.mode() != null && request.mode().equalsIgnoreCase("ADJUST");
        int newQty = adjust ? inv.getQuantity() + request.quantity() : request.quantity();
        if (newQty < 0) {
            throw new BadRequestException("Resulting quantity cannot be negative");
        }
        inv.setQuantity(newQty);
        return toInventoryResponse(inventoryRepository.save(inv), w.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStockResponse getProductStock(Long productId) {
        List<InventoryResponse> rows = inventoryRepository.findByProductId(productId).stream()
                .map(i -> toInventoryResponse(i, warehouseName(i.getWarehouseId())))
                .toList();
        int total = inventoryRepository.totalQuantityForProduct(productId);
        return new ProductStockResponse(productId, total, rows);
    }

    @Override
    @Transactional
    public StockTransferResponse transfer(StockTransferRequest request) {
        if (request.fromWarehouseId().equals(request.toWarehouseId())) {
            throw new BadRequestException("Source and destination warehouses must differ");
        }
        findWarehouse(request.fromWarehouseId());
        findWarehouse(request.toWarehouseId());

        WarehouseInventory source = inventoryRepository
                .findByWarehouseIdAndProductId(request.fromWarehouseId(), request.productId())
                .orElseThrow(() -> new BadRequestException("Source warehouse has no stock for this product"));
        if (source.getQuantity() < request.quantity()) {
            throw new BadRequestException("Insufficient stock at the source warehouse");
        }
        source.setQuantity(source.getQuantity() - request.quantity());
        inventoryRepository.save(source);

        WarehouseInventory dest = inventoryRepository
                .findByWarehouseIdAndProductId(request.toWarehouseId(), request.productId())
                .orElseGet(() -> {
                    WarehouseInventory created = new WarehouseInventory();
                    created.setWarehouseId(request.toWarehouseId());
                    created.setProductId(request.productId());
                    return created;
                });
        dest.setQuantity(dest.getQuantity() + request.quantity());
        inventoryRepository.save(dest);

        StockTransfer t = new StockTransfer();
        t.setFromWarehouseId(request.fromWarehouseId());
        t.setToWarehouseId(request.toWarehouseId());
        t.setProductId(request.productId());
        t.setQuantity(request.quantity());
        t.setStatus(TransferStatus.COMPLETED);
        t.setNote(request.note());
        return WarehouseMapper.toResponse(transferRepository.save(t));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StockTransferResponse> listTransfers(Long productId, Pageable pageable) {
        return PageResponse.from(transferRepository.findByProductId(productId, pageable)
                .map(WarehouseMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> lowStock(Long warehouseId, int threshold) {
        Warehouse w = findWarehouse(warehouseId);
        int t = threshold > 0 ? threshold : 5;
        return inventoryRepository.lowStock(warehouseId, t).stream()
                .map(i -> toInventoryResponse(i, w.getName()))
                .toList();
    }

    // ---- helpers ----

    private Warehouse findWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    private String warehouseName(Long id) {
        return warehouseRepository.findById(id).map(Warehouse::getName).orElse("Unknown");
    }

    private InventoryResponse toInventoryResponse(WarehouseInventory i, String warehouseName) {
        return new InventoryResponse(i.getWarehouseId(), warehouseName, i.getProductId(),
                i.getQuantity(), i.getReserved(), i.getQuantity() - i.getReserved());
    }

    private void apply(Warehouse w, WarehouseRequest r) {
        w.setName(r.name());
        w.setCode(r.code());
        w.setAddressLine(r.addressLine());
        w.setCity(r.city());
        w.setState(r.state());
        w.setCountry(r.country());
        w.setPostalCode(r.postalCode());
        w.setActive(r.active() == null || r.active());
    }
}

package com.shopmart.module.warehouse.service.impl;

import com.shopmart.module.warehouse.entity.WarehouseInventory;
import com.shopmart.module.warehouse.repository.WarehouseInventoryRepository;
import com.shopmart.module.warehouse.service.InventoryAllocator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WarehouseInventoryAllocator implements InventoryAllocator {

    private final WarehouseInventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void allocate(Long productId, int quantity) {
        List<WarehouseInventory> rows = inventoryRepository.findByProductIdOrderByQuantityDesc(productId);
        int remaining = quantity;
        for (WarehouseInventory inv : rows) {
            if (remaining <= 0) break;
            int take = Math.min(inv.getQuantity(), remaining);
            if (take > 0) {
                inv.setQuantity(inv.getQuantity() - take);
                inventoryRepository.save(inv);
                remaining -= take;
            }
        }
        // If remaining > 0 the warehouse layer was short; product.stock already gated availability.
    }

    @Override
    @Transactional
    public void release(Long productId, int quantity) {
        List<WarehouseInventory> rows = inventoryRepository.findByProductIdOrderByQuantityDesc(productId);
        if (rows.isEmpty()) return;
        WarehouseInventory target = rows.get(0);
        target.setQuantity(target.getQuantity() + quantity);
        inventoryRepository.save(target);
    }
}

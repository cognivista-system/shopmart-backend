package com.shopmart.module.warehouse.service;

/**
 * Hook used by order fulfilment to keep per-warehouse inventory in sync.
 * Implementations are best-effort: if no warehouse inventory exists for a product,
 * calls are no-ops (product.stock remains the authoritative availability gate).
 */
public interface InventoryAllocator {
    void allocate(Long productId, int quantity);
    void release(Long productId, int quantity);
}

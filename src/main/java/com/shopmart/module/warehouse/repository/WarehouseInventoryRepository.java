package com.shopmart.module.warehouse.repository;

import com.shopmart.module.warehouse.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {
    Optional<WarehouseInventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    List<WarehouseInventory> findByWarehouseId(Long warehouseId);
    List<WarehouseInventory> findByProductId(Long productId);
    List<WarehouseInventory> findByProductIdOrderByQuantityDesc(Long productId);

    @Query("select coalesce(sum(i.quantity), 0) from WarehouseInventory i where i.productId = :productId")
    int totalQuantityForProduct(@Param("productId") Long productId);

    @Query("select i from WarehouseInventory i where i.warehouseId = :warehouseId and i.quantity <= :threshold")
    List<WarehouseInventory> lowStock(@Param("warehouseId") Long warehouseId, @Param("threshold") int threshold);
}

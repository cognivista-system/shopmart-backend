package com.shopmart.module.warehouse.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouse_inventory",
        uniqueConstraints = @UniqueConstraint(name = "uk_wh_inventory", columnNames = {"warehouse_id", "product_id"}),
        indexes = @Index(name = "idx_wh_inventory_product", columnList = "product_id"))
public class WarehouseInventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity = 0;

    /** Units reserved for unfulfilled orders. */
    @Column(nullable = false)
    private int reserved = 0;
}

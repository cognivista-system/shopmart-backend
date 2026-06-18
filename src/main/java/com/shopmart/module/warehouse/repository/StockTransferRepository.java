package com.shopmart.module.warehouse.repository;

import com.shopmart.module.warehouse.entity.StockTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    Page<StockTransfer> findByProductId(Long productId, Pageable pageable);
}

package com.shopmart.module.vendor.repository;

import com.shopmart.module.vendor.entity.Payout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    Page<Payout> findByVendorId(Long vendorId, Pageable pageable);

    @Query("select coalesce(sum(p.amount), 0) from Payout p " +
           "where p.vendorId = :vendorId and p.status = com.shopmart.module.vendor.entity.PayoutStatus.PAID")
    BigDecimal totalPaid(@Param("vendorId") Long vendorId);
}

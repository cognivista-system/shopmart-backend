package com.shopmart.module.vendor.repository;

import com.shopmart.module.vendor.entity.Vendor;
import com.shopmart.module.vendor.entity.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    Optional<Vendor> findBySlug(String slug);
    boolean existsByUserId(Long userId);
    boolean existsBySlug(String slug);
    Page<Vendor> findByStatus(VendorStatus status, Pageable pageable);
}

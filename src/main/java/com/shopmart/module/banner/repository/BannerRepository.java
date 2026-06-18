package com.shopmart.module.banner.repository;

import com.shopmart.module.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByActiveTrueOrderByPositionAsc();
    List<Banner> findAllByOrderByPositionAsc();
}

package com.shopmart.module.recommendation.repository;

import com.shopmart.module.recommendation.entity.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
}

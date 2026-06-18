package com.shopmart.module.review.repository;

import com.shopmart.module.review.entity.Review;
import com.shopmart.module.review.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductIdAndStatus(Long productId, ReviewStatus status, Pageable pageable);
    Optional<Review> findByIdAndUserId(Long id, Long userId);
    boolean existsByProductIdAndUserId(Long productId, Long userId);
    long countByProductIdAndStatus(Long productId, ReviewStatus status);

    @Query("select coalesce(avg(r.rating), 0) from Review r " +
           "where r.productId = :productId and r.status = com.shopmart.module.review.entity.ReviewStatus.APPROVED")
    double averageRating(@Param("productId") Long productId);
}

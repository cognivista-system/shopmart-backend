package com.shopmart.module.returns.repository;

import com.shopmart.module.returns.entity.ReturnRequest;
import com.shopmart.module.returns.entity.ReturnRequest.ReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<ReturnRequest> findByIdAndUserId(Long id, Long userId);
    List<ReturnRequest> findByStatusOrderByCreatedAtDesc(ReturnStatus status);
    boolean existsByOrderIdAndStatusIn(Long orderId, List<ReturnStatus> statuses);
}

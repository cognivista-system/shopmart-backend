package com.shopmart.module.order.repository;

import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);

    // ---- analytics (Phase 2) ----
    @Query("select coalesce(sum(o.total), 0) from Order o " +
           "where o.paymentStatus = com.shopmart.module.order.entity.PaymentStatus.PAID")
    BigDecimal totalRevenue();

    long countByStatus(OrderStatus status);

    @Query("select o.status, count(o) from Order o group by o.status")
    List<Object[]> countGroupedByStatus();

    List<Order> findByCreatedAtGreaterThanEqual(Instant since);

    List<Order> findTop10ByOrderByCreatedAtDesc();

    // ---- reports (Phase 3) ----
    List<Order> findByCreatedAtBetween(Instant from, Instant to);

    @Query("""
            select o.userId, count(o), sum(o.total)
            from Order o
            where o.paymentStatus = com.shopmart.module.order.entity.PaymentStatus.PAID
              and o.createdAt between :from and :to
            group by o.userId order by sum(o.total) desc
            """)
    List<Object[]> topCustomers(@Param("from") Instant from, @Param("to") Instant to, Pageable pageable);

    @Query("select count(distinct o.userId) from Order o")
    long countDistinctCustomers();

    @Query("select o.userId from Order o group by o.userId having count(o) > 1")
    java.util.List<Long> repeatCustomerIds();
}

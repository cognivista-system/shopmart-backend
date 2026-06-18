package com.shopmart.module.order.repository;

import com.shopmart.module.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            select case when count(oi) > 0 then true else false end
            from OrderItem oi
            where oi.order.userId = :userId and oi.productId = :productId
            """)
    boolean existsForUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    // Best-selling products by total quantity sold (Phase 2 analytics)
    @Query("""
            select oi.productId as productId, oi.productName as productName,
                   sum(oi.quantity) as unitsSold, sum(oi.lineTotal) as revenue
            from OrderItem oi
            where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED
            group by oi.productId, oi.productName
            order by sum(oi.quantity) desc
            """)
    List<Object[]> topProducts(Pageable pageable);

    // ---- recommendations: products co-purchased with a given product ----
    @Query("""
            select oi.productId, count(oi)
            from OrderItem oi
            where oi.order.id in (select oi2.order.id from OrderItem oi2 where oi2.productId = :productId)
              and oi.productId <> :productId
            group by oi.productId
            order by count(oi) desc
            """)
    List<Object[]> coPurchased(@Param("productId") Long productId, Pageable pageable);

    // ---- vendor ----
    Page<OrderItem> findByVendorId(Long vendorId, Pageable pageable);

    @Query("""
            select coalesce(sum(oi.lineTotal), 0)
            from OrderItem oi
            where oi.vendorId = :vendorId
              and oi.order.paymentStatus = com.shopmart.module.order.entity.PaymentStatus.PAID
            """)
    BigDecimal vendorGrossSales(@Param("vendorId") Long vendorId);

    // ---- reports: revenue grouped by product attributes within a date range ----
    @Query("""
            select c.name, sum(oi.lineTotal), sum(oi.quantity)
            from OrderItem oi
            join Product p on p.id = oi.productId
            join p.category c
            where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED
              and oi.order.createdAt between :from and :to
            group by c.name order by sum(oi.lineTotal) desc
            """)
    List<Object[]> revenueByCategory(@Param("from") Instant from, @Param("to") Instant to);

    @Query("""
            select b.name, sum(oi.lineTotal), sum(oi.quantity)
            from OrderItem oi
            join Product p on p.id = oi.productId
            join p.brand b
            where oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED
              and oi.order.createdAt between :from and :to
            group by b.name order by sum(oi.lineTotal) desc
            """)
    List<Object[]> revenueByBrand(@Param("from") Instant from, @Param("to") Instant to);

    @Query("""
            select oi.vendorId, sum(oi.lineTotal), sum(oi.quantity)
            from OrderItem oi
            where oi.vendorId is not null
              and oi.order.status <> com.shopmart.module.order.entity.OrderStatus.CANCELLED
              and oi.order.createdAt between :from and :to
            group by oi.vendorId order by sum(oi.lineTotal) desc
            """)
    List<Object[]> revenueByVendor(@Param("from") Instant from, @Param("to") Instant to);
}

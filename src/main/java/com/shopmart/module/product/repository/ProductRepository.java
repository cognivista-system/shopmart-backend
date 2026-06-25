package com.shopmart.module.product.repository;

import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);

    // ---- analytics (Phase 2) ----
    long countByStockLessThanEqual(int threshold);
    List<Product> findByStockLessThanEqualOrderByStockAsc(int threshold);

    // ---- vendor (Phase 3) ----
    Page<Product> findByVendorId(Long vendorId, Pageable pageable);
    long countByVendorId(Long vendorId);

    long countByStatus(ProductStatus status);
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
    long countByVendorIdAndStatus(Long vendorId, ProductStatus status);

    // ---- search (Phase 3) ----
    @Query("""
            select p from Product p
            where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE
              and (:q = '' or lower(p.name) like lower(concat('%', :q, '%'))
                   or lower(p.description) like lower(concat('%', :q, '%')))
              and (:categoryId is null or p.category.id = :categoryId)
              and (:brandId is null or p.brand.id = :brandId)
            order by p.featured desc, p.ratingAverage desc
            """)
    Page<Product> searchActive(@Param("q") String q,
                               @Param("categoryId") Long categoryId,
                               @Param("brandId") Long brandId,
                               Pageable pageable);

    List<Product> findTop8ByNameContainingIgnoreCaseAndStatus(String name, ProductStatus status);

    @Query("""
            select c.name, count(p) from Product p join p.category c
            where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE
              and (:q = '' or lower(p.name) like lower(concat('%', :q, '%'))
                   or lower(p.description) like lower(concat('%', :q, '%')))
            group by c.name order by count(p) desc
            """)
    List<Object[]> facetByCategory(@Param("q") String q);

    @Query("""
            select b.name, count(p) from Product p join p.brand b
            where p.status = com.shopmart.module.product.entity.ProductStatus.ACTIVE
              and (:q = '' or lower(p.name) like lower(concat('%', :q, '%'))
                   or lower(p.description) like lower(concat('%', :q, '%')))
            group by b.name order by count(p) desc
            """)
    List<Object[]> facetByBrand(@Param("q") String q);

    // ---- recommendations (Phase 3) ----
    List<Product> findTop12ByCategory_IdAndStatusAndIdNot(Long categoryId, ProductStatus status, Long excludeId);
    List<Product> findByIdInAndStatus(List<Long> ids, ProductStatus status);

    // ---- reports (Phase 3) ----
    @Query("select coalesce(sum(p.price * p.stock), 0) from Product p")
    java.math.BigDecimal totalStockValue();
}

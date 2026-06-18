package com.shopmart.module.vendor.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vendors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vendors_user", columnNames = "user_id"),
                @UniqueConstraint(name = "uk_vendors_slug", columnNames = "slug")
        })
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String slug;

    @Column(length = 2000)
    private String description;

    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorStatus status = VendorStatus.PENDING;

    /** Platform commission percentage retained from this vendor's sales. */
    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00");

    private String contactEmail;
    private String contactPhone;
}

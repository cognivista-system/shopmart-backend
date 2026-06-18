package com.shopmart.module.warehouse.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "warehouses", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Column(nullable = false)
    private boolean active = true;
}

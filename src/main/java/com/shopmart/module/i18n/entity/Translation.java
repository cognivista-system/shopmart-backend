package com.shopmart.module.i18n.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "translations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_translation",
                columnNames = {"entity_type", "entity_id", "locale", "field"}),
        indexes = @Index(name = "idx_translation_lookup", columnList = "entity_type, entity_id, locale"))
public class Translation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;   // e.g. "product", "category"

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 16)
    private String locale;       // e.g. "hi", "gu", "fr"

    @Column(nullable = false, length = 64)
    private String field;        // e.g. "name", "description"

    @Column(nullable = false, length = 4000)
    private String value;
}

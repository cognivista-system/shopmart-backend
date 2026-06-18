package com.shopmart.module.banner.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "banners", indexes = @Index(name = "idx_banners_active", columnList = "active, position"))
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    /** Lower numbers show first. */
    @ColumnDefault("0")
    @Column(nullable = false)
    private int position = 0;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean active = true;

    /** Optional scheduling window; null means no bound. */
    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;
}

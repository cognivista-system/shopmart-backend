package com.shopmart.module.search.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "search_logs", indexes = @Index(name = "idx_search_logs_user", columnList = "user_id"))
public class SearchLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "query_text", nullable = false)
    private String queryText;

    @Column(name = "result_count", nullable = false)
    private long resultCount;
}

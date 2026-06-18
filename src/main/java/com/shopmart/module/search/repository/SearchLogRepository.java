package com.shopmart.module.search.repository;

import com.shopmart.module.search.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    List<SearchLog> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            select s.queryText, count(s) from SearchLog s
            where s.createdAt >= :since
            group by s.queryText order by count(s) desc
            """)
    List<Object[]> trending(@Param("since") Instant since, org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Query("delete from SearchLog s where s.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

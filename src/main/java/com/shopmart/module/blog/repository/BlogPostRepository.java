package com.shopmart.module.blog.repository;

import com.shopmart.module.blog.entity.BlogPost;
import com.shopmart.module.blog.entity.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Optional<BlogPost> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<BlogPost> findByStatus(BlogStatus status, Pageable pageable);

    @Query("select distinct p from BlogPost p join p.tags t " +
           "where p.status = :status and t = :tag")
    Page<BlogPost> findByStatusAndTag(@Param("status") BlogStatus status,
                                      @Param("tag") String tag, Pageable pageable);
}

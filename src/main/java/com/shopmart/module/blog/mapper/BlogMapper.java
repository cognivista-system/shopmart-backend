package com.shopmart.module.blog.mapper;

import com.shopmart.module.blog.dto.BlogPostResponse;
import com.shopmart.module.blog.dto.BlogPostSummary;
import com.shopmart.module.blog.entity.BlogPost;

public final class BlogMapper {
    private BlogMapper() {}

    public static BlogPostResponse toResponse(BlogPost p) {
        return new BlogPostResponse(p.getId(), p.getTitle(), p.getSlug(), p.getExcerpt(),
                p.getContent(), p.getCoverImage(), p.getAuthorId(), p.getStatus().name(),
                p.getTags(), p.getPublishedAt(), p.getCreatedAt());
    }

    public static BlogPostSummary toSummary(BlogPost p) {
        return new BlogPostSummary(p.getId(), p.getTitle(), p.getSlug(), p.getExcerpt(),
                p.getCoverImage(), p.getTags(), p.getPublishedAt());
    }
}

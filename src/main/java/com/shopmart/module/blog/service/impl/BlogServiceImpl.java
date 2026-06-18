package com.shopmart.module.blog.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.blog.dto.BlogPostRequest;
import com.shopmart.module.blog.dto.BlogPostResponse;
import com.shopmart.module.blog.dto.BlogPostSummary;
import com.shopmart.module.blog.entity.BlogPost;
import com.shopmart.module.blog.entity.BlogStatus;
import com.shopmart.module.blog.mapper.BlogMapper;
import com.shopmart.module.blog.repository.BlogPostRepository;
import com.shopmart.module.blog.service.BlogService;
import com.shopmart.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostRepository repository;

    @Override
    @Transactional
    public BlogPostResponse create(Long authorId, BlogPostRequest request) {
        BlogPost post = new BlogPost();
        post.setSlug(uniqueSlug(SlugUtils.slugify(request.title())));
        post.setAuthorId(authorId);
        apply(post, request);
        return BlogMapper.toResponse(repository.save(post));
    }

    @Override
    @Transactional
    public BlogPostResponse update(Long id, BlogPostRequest request) {
        BlogPost post = find(id);
        apply(post, request);
        return BlogMapper.toResponse(repository.save(post));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostResponse getBySlug(String slug) {
        BlogPost post = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post", "slug", slug));
        if (post.getStatus() != BlogStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Blog post", "slug", slug);
        }
        return BlogMapper.toResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostResponse getById(Long id) {
        return BlogMapper.toResponse(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostSummary> listPublished(String tag, Pageable pageable) {
        Page<BlogPost> page = (tag == null || tag.isBlank())
                ? repository.findByStatus(BlogStatus.PUBLISHED, pageable)
                : repository.findByStatusAndTag(BlogStatus.PUBLISHED, tag, pageable);
        return PageResponse.from(page.map(BlogMapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlogPostResponse> listAll(Pageable pageable) {
        return PageResponse.from(repository.findAll(pageable).map(BlogMapper::toResponse));
    }

    // ---- helpers ----

    private BlogPost find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post", "id", id));
    }

    private String uniqueSlug(String base) {
        if (base == null || base.isBlank()) base = "post";
        return repository.existsBySlug(base)
                ? base + "-" + UUID.randomUUID().toString().substring(0, 6)
                : base;
    }

    private void apply(BlogPost post, BlogPostRequest r) {
        post.setTitle(r.title());
        post.setExcerpt(r.excerpt());
        post.setContent(r.content());
        post.setCoverImage(r.coverImage());
        post.setTags(r.tags() != null ? new HashSet<>(r.tags()) : new HashSet<>());

        BlogStatus newStatus = parseStatus(r.status());
        boolean publishingNow = newStatus == BlogStatus.PUBLISHED && post.getStatus() != BlogStatus.PUBLISHED;
        post.setStatus(newStatus);
        if (publishingNow) {
            post.setPublishedAt(Instant.now());
        } else if (newStatus == BlogStatus.DRAFT) {
            post.setPublishedAt(null);
        }
    }

    private BlogStatus parseStatus(String status) {
        if (status == null) return BlogStatus.DRAFT;
        try {
            return BlogStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BlogStatus.DRAFT;
        }
    }
}

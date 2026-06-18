package com.shopmart.module.category.service.impl;

import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.category.dto.CategoryRequest;
import com.shopmart.module.category.dto.CategoryResponse;
import com.shopmart.module.category.entity.Category;
import com.shopmart.module.category.mapper.CategoryMapper;
import com.shopmart.module.category.repository.CategoryRepository;
import com.shopmart.module.category.service.CategoryService;
import com.shopmart.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    @CacheEvict(cacheNames = "categories", allEntries = true)
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = SlugUtils.slugify(request.name());
        if (repository.existsBySlug(slug)) {
            throw new ConflictException("A category with a similar name already exists");
        }
        Category c = new Category();
        apply(c, request, slug);
        return CategoryMapper.toResponse(repository.save(c));
    }

    @Override
    @CacheEvict(cacheNames = "categories", allEntries = true)
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category c = find(id);
        apply(c, request, c.getSlug());
        return CategoryMapper.toResponse(repository.save(c));
    }

    @Override
    @CacheEvict(cacheNames = "categories", allEntries = true)
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Override
    @Cacheable(cacheNames = "categories", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryMapper.toResponse(find(id));
    }

    @Override
    @Cacheable(cacheNames = "categories", key = "'all'")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return repository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    private Category find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private void apply(Category c, CategoryRequest r, String slug) {
        c.setName(r.name());
        c.setSlug(slug);
        c.setDescription(r.description());
        c.setBannerUrl(r.bannerUrl());
        c.setMetaTitle(r.metaTitle());
        c.setMetaDescription(r.metaDescription());
        c.setActive(r.active() == null || r.active());
    }
}

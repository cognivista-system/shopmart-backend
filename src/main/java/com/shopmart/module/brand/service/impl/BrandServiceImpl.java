package com.shopmart.module.brand.service.impl;

import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.brand.dto.BrandRequest;
import com.shopmart.module.brand.dto.BrandResponse;
import com.shopmart.module.brand.entity.Brand;
import com.shopmart.module.brand.mapper.BrandMapper;
import com.shopmart.module.brand.repository.BrandRepository;
import com.shopmart.module.brand.service.BrandService;
import com.shopmart.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository repository;

    @Override
    @CacheEvict(cacheNames = "brands", allEntries = true)
    @Transactional
    public BrandResponse create(BrandRequest request) {
        String slug = SlugUtils.slugify(request.name());
        if (repository.existsBySlug(slug)) {
            throw new ConflictException("A brand with a similar name already exists");
        }
        Brand b = new Brand();
        apply(b, request, slug);
        return BrandMapper.toResponse(repository.save(b));
    }

    @Override
    @CacheEvict(cacheNames = "brands", allEntries = true)
    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand b = find(id);
        apply(b, request, b.getSlug());
        return BrandMapper.toResponse(repository.save(b));
    }

    @Override
    @CacheEvict(cacheNames = "brands", allEntries = true)
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Override
    @Cacheable(cacheNames = "brands", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        return BrandMapper.toResponse(find(id));
    }

    @Override
    @Cacheable(cacheNames = "brands", key = "'all'")
    @Transactional(readOnly = true)
    public List<BrandResponse> getAll() {
        return repository.findAll().stream().map(BrandMapper::toResponse).toList();
    }

    private Brand find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", id));
    }

    private void apply(Brand b, BrandRequest r, String slug) {
        b.setName(r.name());
        b.setSlug(slug);
        b.setDescription(r.description());
        b.setLogoUrl(r.logoUrl());
        b.setBannerUrl(r.bannerUrl());
        b.setMetaTitle(r.metaTitle());
        b.setMetaDescription(r.metaDescription());
        b.setActive(r.active() == null || r.active());
    }
}

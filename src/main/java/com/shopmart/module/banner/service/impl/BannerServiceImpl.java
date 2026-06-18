package com.shopmart.module.banner.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.banner.dto.BannerRequest;
import com.shopmart.module.banner.dto.BannerResponse;
import com.shopmart.module.banner.entity.Banner;
import com.shopmart.module.banner.mapper.BannerMapper;
import com.shopmart.module.banner.repository.BannerRepository;
import com.shopmart.module.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> listActive() {
        Instant now = Instant.now();
        return repository.findByActiveTrueOrderByPositionAsc().stream()
                .filter(b -> b.getStartsAt() == null || !now.isBefore(b.getStartsAt()))
                .filter(b -> b.getEndsAt() == null || !now.isAfter(b.getEndsAt()))
                .map(BannerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> listAll() {
        return repository.findAllByOrderByPositionAsc().stream().map(BannerMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponse getById(Long id) {
        return BannerMapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public BannerResponse create(BannerRequest request) {
        Banner b = new Banner();
        apply(b, request);
        return BannerMapper.toResponse(repository.save(b));
    }

    @Override
    @Transactional
    public BannerResponse update(Long id, BannerRequest request) {
        Banner b = find(id);
        apply(b, request);
        return BannerMapper.toResponse(repository.save(b));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Banner find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
    }

    private void apply(Banner b, BannerRequest r) {
        b.setTitle(r.title());
        b.setSubtitle(r.subtitle());
        b.setImageUrl(r.imageUrl());
        b.setLinkUrl(r.linkUrl());
        b.setPosition(r.position() != null ? r.position() : 0);
        b.setActive(r.active() == null || r.active());
        b.setStartsAt(r.startsAt());
        b.setEndsAt(r.endsAt());
    }
}

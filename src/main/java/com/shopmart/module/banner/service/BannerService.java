package com.shopmart.module.banner.service;

import com.shopmart.module.banner.dto.BannerRequest;
import com.shopmart.module.banner.dto.BannerResponse;

import java.util.List;

public interface BannerService {
    List<BannerResponse> listActive();          // public: active + within schedule window
    List<BannerResponse> listAll();              // admin
    BannerResponse getById(Long id);
    BannerResponse create(BannerRequest request);
    BannerResponse update(Long id, BannerRequest request);
    void delete(Long id);
}

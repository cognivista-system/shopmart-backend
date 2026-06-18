package com.shopmart.module.brand.service;

import com.shopmart.module.brand.dto.BrandRequest;
import com.shopmart.module.brand.dto.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse create(BrandRequest request);
    BrandResponse update(Long id, BrandRequest request);
    void delete(Long id);
    BrandResponse getById(Long id);
    List<BrandResponse> getAll();
}

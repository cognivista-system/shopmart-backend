package com.shopmart.module.search.dto;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.product.dto.ProductSummary;

import java.util.List;

public record SearchResultResponse(
        String query,
        PageResponse<ProductSummary> results,
        List<FacetCount> categoryFacets,
        List<FacetCount> brandFacets,
        List<String> suggestions
) {}

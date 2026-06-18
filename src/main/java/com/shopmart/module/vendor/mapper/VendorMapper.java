package com.shopmart.module.vendor.mapper;

import com.shopmart.module.vendor.dto.PayoutResponse;
import com.shopmart.module.vendor.dto.VendorResponse;
import com.shopmart.module.vendor.entity.Payout;
import com.shopmart.module.vendor.entity.Vendor;

public final class VendorMapper {
    private VendorMapper() {}

    public static VendorResponse toResponse(Vendor v) {
        return new VendorResponse(v.getId(), v.getUserId(), v.getStoreName(), v.getSlug(),
                v.getDescription(), v.getLogoUrl(), v.getStatus().name(), v.getCommissionRate(),
                v.getContactEmail(), v.getContactPhone());
    }

    public static PayoutResponse toResponse(Payout p) {
        return new PayoutResponse(p.getId(), p.getVendorId(), p.getAmount(), p.getStatus().name(),
                p.getPeriodStart(), p.getPeriodEnd(), p.getPaidAt(), p.getNote(), p.getCreatedAt());
    }
}

package com.shopmart.module.returns.mapper;

import com.shopmart.module.returns.dto.ReturnResponse;
import com.shopmart.module.returns.entity.ReturnRequest;

public final class ReturnMapper {
    private ReturnMapper() {}

    public static ReturnResponse toResponse(ReturnRequest r) {
        return new ReturnResponse(r.getId(), r.getOrderId(), r.getUserId(), r.getReason(),
                r.getStatus().name(), r.getRefundAmount(), r.getAdminNote(),
                r.getGatewayRefundId(), r.getProcessedAt(), r.getCreatedAt());
    }
}

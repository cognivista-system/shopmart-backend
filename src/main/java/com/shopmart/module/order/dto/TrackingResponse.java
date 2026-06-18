package com.shopmart.module.order.dto;

import java.time.Instant;
import java.util.List;

public record TrackingResponse(
        String orderNumber,
        String currentStatus,
        List<Step> timeline
) {
    public record Step(String status, String label, boolean reached, Instant at) {}
}

package com.shopmart.module.superadmin.dto;

import java.math.BigDecimal;

public record SuperAdminDashboardResponse(
        long totalAdmins,
        long totalProducts,
        long pendingApproval,
        long totalOrders,
        BigDecimal revenue,
        long totalCustomers
) {}

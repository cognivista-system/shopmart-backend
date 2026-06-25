package com.shopmart.module.superadmin.service;

import com.shopmart.module.superadmin.dto.AdminCreatedResponse;
import com.shopmart.module.superadmin.dto.CreateAdminRequest;
import com.shopmart.module.superadmin.dto.SuperAdminDashboardResponse;

public interface SuperAdminService {
    AdminCreatedResponse createAdmin(CreateAdminRequest request);
    SuperAdminDashboardResponse dashboard();
}

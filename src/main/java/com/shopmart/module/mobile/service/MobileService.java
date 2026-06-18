package com.shopmart.module.mobile.service;

import com.shopmart.module.mobile.dto.DeviceRegisterRequest;
import com.shopmart.module.mobile.dto.MobileConfigResponse;

public interface MobileService {
    MobileConfigResponse config();
    Long registerDevice(Long userId, DeviceRegisterRequest request);
    void unregisterDevice(String token);
}

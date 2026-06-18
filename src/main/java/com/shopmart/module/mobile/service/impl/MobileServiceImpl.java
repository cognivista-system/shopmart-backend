package com.shopmart.module.mobile.service.impl;

import com.shopmart.module.currency.service.CurrencyService;
import com.shopmart.module.mobile.dto.DeviceRegisterRequest;
import com.shopmart.module.mobile.dto.MobileConfigResponse;
import com.shopmart.module.mobile.entity.DeviceToken;
import com.shopmart.module.mobile.repository.DeviceTokenRepository;
import com.shopmart.module.mobile.service.MobileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MobileServiceImpl implements MobileService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final CurrencyService currencyService;

    @Value("${app.mobile.latest-version:1.0.0}")
    private String latestVersion;
    @Value("${app.mobile.min-version:1.0.0}")
    private String minVersion;
    @Value("${app.i18n.supported:en,hi,gu,fr,es}")
    private String locales;

    @Override
    public MobileConfigResponse config() {
        List<String> currencies = currencyService.list().stream().map(c -> c.code()).toList();
        return new MobileConfigResponse(
                "ShopMart", latestVersion, minVersion, false,
                currencyService.base(), currencies,
                Arrays.stream(locales.split(",")).map(String::trim).toList(),
                Map.of("reviews", true, "loyalty", true, "vendorMarketplace", true, "multiCurrency", true)
        );
    }

    @Override
    @Transactional
    public Long registerDevice(Long userId, DeviceRegisterRequest request) {
        DeviceToken d = deviceTokenRepository.findByToken(request.token()).orElseGet(DeviceToken::new);
        d.setUserId(userId);
        d.setToken(request.token());
        d.setPlatform(request.platform());
        d.setAppVersion(request.appVersion());
        return deviceTokenRepository.save(d).getId();
    }

    @Override
    @Transactional
    public void unregisterDevice(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(deviceTokenRepository::delete);
    }
}

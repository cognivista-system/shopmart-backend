package com.shopmart.module.mobile.dto;

import java.util.List;
import java.util.Map;

public record MobileConfigResponse(
        String appName,
        String latestVersion,
        String minSupportedVersion,
        boolean forceUpdate,
        String baseCurrency,
        List<String> currencies,
        List<String> locales,
        Map<String, Boolean> featureFlags
) {}

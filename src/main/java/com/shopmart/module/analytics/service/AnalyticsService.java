package com.shopmart.module.analytics.service;

import com.shopmart.module.analytics.dto.*;

import java.util.List;

public interface AnalyticsService {
    DashboardResponse dashboard();
    List<SalesPoint> salesOverTime(int days);
    List<TopProduct> topProducts(int limit);
    List<StatusCount> orderStatusBreakdown();
    List<LowStockProduct> lowStock(int threshold);
    CustomerAnalyticsResponse customerAnalytics();
}

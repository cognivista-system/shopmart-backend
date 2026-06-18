package com.shopmart.module.report.service;

import com.shopmart.module.report.dto.*;

import java.time.Instant;
import java.util.List;

public interface ReportService {
    SalesReportResponse salesReport(Instant from, Instant to);
    List<RevenueRow> revenueByCategory(Instant from, Instant to);
    List<RevenueRow> revenueByBrand(Instant from, Instant to);
    List<RevenueRow> revenueByVendor(Instant from, Instant to);
    List<CustomerRow> topCustomers(Instant from, Instant to, int limit);
    InventoryReportResponse inventoryReport(int lowStockThreshold);
    OrderReportResponse orderReport(Instant from, Instant to);
    List<CommissionRow> commissionReport();
}

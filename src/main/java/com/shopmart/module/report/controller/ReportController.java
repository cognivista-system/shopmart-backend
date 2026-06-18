package com.shopmart.module.report.controller;

import com.shopmart.common.dto.ApiResponse;
import com.shopmart.module.report.dto.*;
import com.shopmart.module.report.mapper.CsvWriter;
import com.shopmart.module.report.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports")
public class ReportController {

    private final ReportService service;

    @GetMapping("/sales")
    public ResponseEntity<?> sales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        SalesReportResponse report = service.salesReport(range[0], range[1]);
        if (isCsv(format)) {
            List<List<Object>> rows = new ArrayList<>();
            for (SalesReportRow r : report.daily()) {
                rows.add(List.of(r.date(), r.revenue(), r.orders()));
            }
            return csv("sales-report.csv", CsvWriter.build(List.of("date", "revenue", "orders"), rows));
        }
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/revenue-by-category")
    public ResponseEntity<?> revenueByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        return revenueResponse(service.revenueByCategory(range[0], range[1]), "category", format,
                "revenue-by-category.csv");
    }

    @GetMapping("/revenue-by-brand")
    public ResponseEntity<?> revenueByBrand(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        return revenueResponse(service.revenueByBrand(range[0], range[1]), "brand", format,
                "revenue-by-brand.csv");
    }

    @GetMapping("/revenue-by-vendor")
    public ResponseEntity<?> revenueByVendor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        return revenueResponse(service.revenueByVendor(range[0], range[1]), "vendor", format,
                "revenue-by-vendor.csv");
    }

    @GetMapping("/top-customers")
    public ResponseEntity<?> topCustomers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        List<CustomerRow> data = service.topCustomers(range[0], range[1], limit);
        if (isCsv(format)) {
            List<List<Object>> rows = new ArrayList<>();
            for (CustomerRow r : data) {
                rows.add(List.of(r.userId(), r.name(), r.orders(), r.totalSpent()));
            }
            return csv("top-customers.csv",
                    CsvWriter.build(List.of("userId", "name", "orders", "totalSpent"), rows));
        }
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/inventory")
    public ResponseEntity<?> inventory(
            @RequestParam(defaultValue = "5") int threshold,
            @RequestParam(required = false) String format) {
        InventoryReportResponse report = service.inventoryReport(threshold);
        if (isCsv(format)) {
            List<List<Object>> rows = new ArrayList<>();
            for (LowStockRow r : report.lowStock()) {
                rows.add(List.of(r.id(), r.name(), r.stock()));
            }
            return csv("inventory-low-stock.csv", CsvWriter.build(List.of("id", "name", "stock"), rows));
        }
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/orders")
    public ResponseEntity<?> orders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String format) {
        Instant[] range = range(from, to);
        OrderReportResponse report = service.orderReport(range[0], range[1]);
        if (isCsv(format)) {
            List<List<Object>> rows = new ArrayList<>();
            for (StatusRow r : report.byStatus()) {
                rows.add(List.of(r.status(), r.count()));
            }
            return csv("order-report.csv", CsvWriter.build(List.of("status", "count"), rows));
        }
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    // ---- helpers ----

    private ResponseEntity<?> revenueResponse(List<RevenueRow> data, String labelHeader,
                                              String format, String filename) {
        if (isCsv(format)) {
            List<List<Object>> rows = new ArrayList<>();
            for (RevenueRow r : data) {
                rows.add(List.of(r.label(), r.revenue(), r.unitsSold()));
            }
            return csv(filename, CsvWriter.build(List.of(labelHeader, "revenue", "unitsSold"), rows));
        }
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    private boolean isCsv(String format) {
        return format != null && format.equalsIgnoreCase("csv");
    }

    private ResponseEntity<String> csv(String filename, String body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    /** Resolves an inclusive [start-of-day, end-of-day] UTC range, defaulting to the last 30 days. */
    private Instant[] range(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate start = from != null ? from : today.minusDays(30);
        LocalDate end = to != null ? to : today;
        Instant fromInstant = start.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant toInstant = end.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
        return new Instant[]{fromInstant, toInstant};
    }

    @GetMapping("/commission")
    public ResponseEntity<?> commission(@RequestParam(required = false) String format) {
        List<CommissionRow> rows = service.commissionReport();
        if (isCsv(format)) {
            List<List<Object>> data = new ArrayList<>();
            for (CommissionRow r : rows) {
                data.add(List.of(r.vendorId(), r.storeName(), r.grossSales(), r.commissionRate(), r.commission(), r.netEarnings()));
            }
            return csv("commission-report.csv", CsvWriter.build(
                    List.of("vendorId", "storeName", "grossSales", "commissionRate", "commission", "netEarnings"), data));
        }
        return ResponseEntity.ok(ApiResponse.ok(rows));
    }
}

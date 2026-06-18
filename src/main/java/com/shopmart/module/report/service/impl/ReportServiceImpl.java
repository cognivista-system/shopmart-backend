package com.shopmart.module.report.service.impl;

import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.repository.OrderItemRepository;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.report.dto.*;
import com.shopmart.module.report.service.ReportService;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import com.shopmart.module.vendor.entity.Vendor;
import com.shopmart.module.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional(readOnly = true)
    public SalesReportResponse salesReport(Instant from, Instant to) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);

        Map<LocalDate, BigDecimal> revenueByDay = new TreeMap<>();
        Map<LocalDate, Long> countByDay = new TreeMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        long totalOrders = 0;

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED || o.getCreatedAt() == null) continue;
            LocalDate day = LocalDate.ofInstant(o.getCreatedAt(), ZoneOffset.UTC);
            revenueByDay.merge(day, o.getTotal(), BigDecimal::add);
            countByDay.merge(day, 1L, Long::sum);
            totalRevenue = totalRevenue.add(o.getTotal());
            totalOrders++;
        }

        List<SalesReportRow> daily = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> e : revenueByDay.entrySet()) {
            daily.add(new SalesReportRow(e.getKey(), e.getValue(), countByDay.getOrDefault(e.getKey(), 0L)));
        }
        return new SalesReportResponse(from, to, totalRevenue, totalOrders, daily);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueRow> revenueByCategory(Instant from, Instant to) {
        return toRevenueRows(orderItemRepository.revenueByCategory(from, to));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueRow> revenueByBrand(Instant from, Instant to) {
        return toRevenueRows(orderItemRepository.revenueByBrand(from, to));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueRow> revenueByVendor(Instant from, Instant to) {
        List<RevenueRow> rows = new ArrayList<>();
        for (Object[] r : orderItemRepository.revenueByVendor(from, to)) {
            Long vendorId = (Long) r[0];
            String name = vendorRepository.findById(vendorId).map(Vendor::getStoreName)
                    .orElse("Vendor #" + vendorId);
            rows.add(new RevenueRow(name, (BigDecimal) r[1], ((Number) r[2]).longValue()));
        }
        return rows;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerRow> topCustomers(Instant from, Instant to, int limit) {
        int n = Math.max(1, Math.min(limit, 100));
        List<CustomerRow> rows = new ArrayList<>();
        for (Object[] r : orderRepository.topCustomers(from, to, PageRequest.of(0, n))) {
            Long userId = (Long) r[0];
            long orders = ((Number) r[1]).longValue();
            BigDecimal spent = (BigDecimal) r[2];
            String name = userRepository.findById(userId).map(User::getName).orElse("User #" + userId);
            rows.add(new CustomerRow(userId, name, orders, spent));
        }
        return rows;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryReportResponse inventoryReport(int lowStockThreshold) {
        int t = lowStockThreshold > 0 ? lowStockThreshold : 5;
        long total = productRepository.count();
        BigDecimal value = productRepository.totalStockValue();
        long lowCount = productRepository.countByStockLessThanEqual(t);
        List<LowStockRow> low = productRepository.findByStockLessThanEqualOrderByStockAsc(t).stream()
                .map(p -> new LowStockRow(p.getId(), p.getName(), p.getStock()))
                .toList();
        return new InventoryReportResponse(total, value != null ? value : BigDecimal.ZERO, lowCount, low);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderReportResponse orderReport(Instant from, Instant to) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(from, to);
        Map<String, Long> byStatus = new TreeMap<>();
        for (Order o : orders) {
            byStatus.merge(o.getStatus().name(), 1L, Long::sum);
        }
        List<StatusRow> rows = byStatus.entrySet().stream()
                .map(e -> new StatusRow(e.getKey(), e.getValue()))
                .toList();
        return new OrderReportResponse(from, to, orders.size(), rows);
    }

    // ---- helpers ----

    private List<RevenueRow> toRevenueRows(List<Object[]> rows) {
        List<RevenueRow> out = new ArrayList<>();
        for (Object[] r : rows) {
            out.add(new RevenueRow((String) r[0], (BigDecimal) r[1], ((Number) r[2]).longValue()));
        }
        return out;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<CommissionRow> commissionReport() {
        java.util.List<CommissionRow> rows = new java.util.ArrayList<>();
        for (var v : vendorRepository.findAll()) {
            java.math.BigDecimal gross = orderItemRepository.vendorGrossSales(v.getId());
            if (gross == null) gross = java.math.BigDecimal.ZERO;
            java.math.BigDecimal rate = v.getCommissionRate() != null ? v.getCommissionRate() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal commission = gross.multiply(rate)
                    .divide(java.math.BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            rows.add(new CommissionRow(v.getId(), v.getStoreName(), gross, rate, commission, gross.subtract(commission)));
        }
        return rows;
    }
}

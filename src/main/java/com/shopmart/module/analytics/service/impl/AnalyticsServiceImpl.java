package com.shopmart.module.analytics.service.impl;

import com.shopmart.module.analytics.dto.*;
import com.shopmart.module.analytics.service.AnalyticsService;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.repository.OrderItemRepository;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final int LOW_STOCK_THRESHOLD = 5;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        BigDecimal revenue = orderRepository.totalRevenue();
        long totalOrders = orderRepository.count();
        long pending = orderRepository.countByStatus(OrderStatus.PENDING);
        long customers = userRepository.count();
        long products = productRepository.count();
        long lowStock = productRepository.countByStockLessThanEqual(LOW_STOCK_THRESHOLD);
        return new DashboardResponse(
                revenue != null ? revenue : BigDecimal.ZERO,
                totalOrders, pending, customers, products, lowStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesPoint> salesOverTime(int days) {
        int span = Math.max(1, Math.min(days, 365));
        Instant since = Instant.now().minus(span, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        List<Order> orders = orderRepository.findByCreatedAtGreaterThanEqual(since);

        // Pre-seed every day in range with zero so the series has no gaps
        Map<LocalDate, BigDecimal> revenueByDay = new TreeMap<>();
        Map<LocalDate, Long> countByDay = new TreeMap<>();
        LocalDate start = LocalDate.ofInstant(since, ZoneOffset.UTC);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
            revenueByDay.put(d, BigDecimal.ZERO);
            countByDay.put(d, 0L);
        }

        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.CANCELLED || o.getCreatedAt() == null) continue;
            LocalDate day = LocalDate.ofInstant(o.getCreatedAt(), ZoneOffset.UTC);
            revenueByDay.merge(day, o.getTotal(), BigDecimal::add);
            countByDay.merge(day, 1L, Long::sum);
        }

        List<SalesPoint> series = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> e : revenueByDay.entrySet()) {
            series.add(new SalesPoint(e.getKey(), e.getValue(), countByDay.getOrDefault(e.getKey(), 0L)));
        }
        return series;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProduct> topProducts(int limit) {
        int n = Math.max(1, Math.min(limit, 50));
        List<Object[]> rows = orderItemRepository.topProducts(PageRequest.of(0, n));
        List<TopProduct> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long productId = (Long) row[0];
            String name = (String) row[1];
            long units = ((Number) row[2]).longValue();
            BigDecimal revenue = (BigDecimal) row[3];
            result.add(new TopProduct(productId, name, units, revenue));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusCount> orderStatusBreakdown() {
        List<Object[]> rows = orderRepository.countGroupedByStatus();
        List<StatusCount> result = new ArrayList<>();
        for (Object[] row : rows) {
            OrderStatus status = (OrderStatus) row[0];
            long count = ((Number) row[1]).longValue();
            result.add(new StatusCount(status.name(), count));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockProduct> lowStock(int threshold) {
        int t = threshold > 0 ? threshold : LOW_STOCK_THRESHOLD;
        return productRepository.findByStockLessThanEqualOrderByStockAsc(t).stream()
                .map(p -> new LowStockProduct(p.getId(), p.getName(), p.getStock()))
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public CustomerAnalyticsResponse customerAnalytics() {
        long totalCustomers = userRepository.count();
        long withOrders = orderRepository.countDistinctCustomers();
        long repeat = orderRepository.repeatCustomerIds().size();
        long orderCount = orderRepository.count();
        java.math.BigDecimal revenue = orderRepository.totalRevenue();
        if (revenue == null) revenue = java.math.BigDecimal.ZERO;
        java.math.BigDecimal aov = orderCount == 0 ? java.math.BigDecimal.ZERO
                : revenue.divide(java.math.BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal repeatRate = withOrders == 0 ? java.math.BigDecimal.ZERO
                : java.math.BigDecimal.valueOf(repeat).multiply(java.math.BigDecimal.valueOf(100))
                .divide(java.math.BigDecimal.valueOf(withOrders), 2, java.math.RoundingMode.HALF_UP);
        long new30 = userRepository.countByCreatedAtAfter(
                java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS));
        return new CustomerAnalyticsResponse(totalCustomers, withOrders, repeat, repeatRate, aov, new30);
    }
}

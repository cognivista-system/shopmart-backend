package com.shopmart.module.order.service;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.module.order.dto.CreateOrderRequest;
import com.shopmart.module.order.dto.OrderResponse;
import com.shopmart.module.order.dto.OrderSummary;
import com.shopmart.module.order.dto.TrackingResponse;
import com.shopmart.module.order.entity.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse create(Long userId, CreateOrderRequest request);
    PageResponse<OrderSummary> getOrders(Long userId, Pageable pageable);
    OrderResponse getOrderDetails(Long userId, Long orderId);
    OrderResponse cancel(Long userId, Long orderId);
    TrackingResponse track(Long userId, Long orderId);
    OrderResponse updateStatus(Long orderId, OrderStatus status);
}

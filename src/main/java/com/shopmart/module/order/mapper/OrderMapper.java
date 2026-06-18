package com.shopmart.module.order.mapper;

import com.shopmart.module.order.dto.*;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderItem;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order o) {
        ShippingAddressDto addr = new ShippingAddressDto(o.getShipName(), o.getShipPhone(),
                o.getShipLine1(), o.getShipLine2(), o.getShipCity(), o.getShipState(),
                o.getShipPostalCode(), o.getShipCountry());
        return new OrderResponse(
                o.getId(), o.getOrderNumber(), o.getStatus().name(), o.getPaymentStatus().name(),
                o.getPaymentMethod(), o.getSubtotal(), o.getShippingFee(), o.getDiscount(), o.getTotal(),
                addr, o.getItems().stream().map(OrderMapper::toItem).toList(), o.getCreatedAt());
    }

    public static OrderSummary toSummary(Order o) {
        int count = o.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        return new OrderSummary(o.getId(), o.getOrderNumber(), o.getStatus().name(),
                o.getTotal(), count, o.getCreatedAt());
    }

    private static OrderItemResponse toItem(OrderItem i) {
        return new OrderItemResponse(i.getProductId(), i.getProductName(), i.getThumbnail(),
                i.getQuantity(), i.getUnitPrice(), i.getLineTotal());
    }
}

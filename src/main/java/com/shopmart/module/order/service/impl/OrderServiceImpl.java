package com.shopmart.module.order.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.common.notification.NotificationService;
import com.shopmart.module.cart.entity.Cart;
import com.shopmart.module.cart.entity.CartItem;
import com.shopmart.module.cart.repository.CartRepository;
import com.shopmart.module.order.dto.*;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderItem;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.entity.PaymentStatus;
import com.shopmart.module.order.mapper.OrderMapper;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.order.service.OrderService;
import com.shopmart.module.product.entity.Product;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.coupon.dto.CouponApplication;
import com.shopmart.module.coupon.service.CouponService;
import com.shopmart.module.notification.entity.NotificationType;
import com.shopmart.module.notification.service.UserNotificationService;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import com.shopmart.module.warehouse.service.InventoryAllocator;
import com.shopmart.module.loyalty.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CouponService couponService;
    private final UserNotificationService userNotificationService;
    private final InventoryAllocator inventoryAllocator;
    private final LoyaltyService loyaltyService;

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("499");
    private static final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal("49");
    private static final Set<String> PAYMENT_METHODS = Set.of("COD", "UPI", "CARD", "NETBANKING", "WALLET");
    private static final DateTimeFormatter ORDER_NO_FMT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    @Override
    @Transactional
    public OrderResponse create(Long userId, CreateOrderRequest request) {
        String method = request.paymentMethod().toUpperCase();
        if (!PAYMENT_METHODS.contains(method)) {
            throw new BadRequestException("Unsupported payment method: " + request.paymentMethod());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(generateOrderNumber());
        order.setPaymentMethod(method);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        applyShippingAddress(order, request.shippingAddress());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            Product product = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", ci.getProductId()));
            if (product.getStock() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName());
            }

            BigDecimal lineTotal = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            OrderItem oi = new OrderItem();
            oi.setProductId(product.getId());
            oi.setProductName(product.getName());
            oi.setThumbnail(product.getImages().isEmpty() ? null : product.getImages().get(0).getUrl());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());
            oi.setLineTotal(lineTotal);
            oi.setVendorId(product.getVendorId());
            order.addItem(oi);

            subtotal = subtotal.add(lineTotal);

            // Decrement inventory
            product.setStock(product.getStock() - ci.getQuantity());
            productRepository.save(product);

            // Keep per-warehouse inventory in sync (no-op when no warehouses are configured)
            inventoryAllocator.allocate(product.getId(), ci.getQuantity());
        }

        BigDecimal shippingFee = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO : FLAT_SHIPPING_FEE;

        // Apply coupon if one was supplied
        BigDecimal discount = BigDecimal.ZERO;
        CouponApplication couponApplication = null;
        String couponCode = request.couponCode();
        if (couponCode != null && !couponCode.isBlank()) {
            couponApplication = couponService.validate(couponCode, userId, subtotal);
            discount = couponApplication.discount();
        }

        BigDecimal total = subtotal.add(shippingFee).subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscount(discount);
        order.setTotal(total);

        Order saved = orderRepository.save(order);

        // Empty the cart now that the order is placed
        cart.getItems().clear();
        cartRepository.save(cart);

        // Record coupon redemption (if any) now that we have the order id
        if (couponApplication != null) {
            couponService.markRedeemed(couponApplication.couponId(), userId,
                    saved.getId(), couponApplication.discount());
        }

        // In-app notification + dispatch (email/SMS abstraction)
        userNotificationService.notify(userId, NotificationType.ORDER, "Order placed",
                "Your order " + saved.getOrderNumber() + " has been placed successfully.",
                "/orders/" + saved.getId());

        userRepository.findById(userId).map(User::getEmail)
                .ifPresent(email -> notificationService.sendOrderConfirmation(email, saved.getOrderNumber()));

        // Award loyalty points for the placed order (best-effort; never blocks checkout).
        try {
            loyaltyService.earnForOrder(userId, saved.getId(), saved.getTotal());
        } catch (Exception e) {
            log.warn("[LOYALTY] failed to award points for order {}: {}", saved.getId(), e.getMessage());
        }

        return OrderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummary> getOrders(Long userId, Pageable pageable) {
        Page<OrderSummary> page = orderRepository.findByUserId(userId, pageable).map(OrderMapper::toSummary);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        return OrderMapper.toResponse(findOwned(userId, orderId));
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long userId, Long orderId) {
        Order order = findOwned(userId, orderId);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BadRequestException("This order can no longer be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);

        // Restock items
        for (OrderItem item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(p -> {
                p.setStock(p.getStock() + item.getQuantity());
                productRepository.save(p);
            });
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public TrackingResponse track(Long userId, Long orderId) {
        Order order = findOwned(userId, orderId);
        return buildTracking(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        if (status == OrderStatus.DELIVERED && order.getPaymentStatus() == PaymentStatus.PENDING
                && "COD".equals(order.getPaymentMethod())) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    // ---- helpers ----

    private Order findOwned(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private String generateOrderNumber() {
        return "ORD-" + ORDER_NO_FMT.format(Instant.now()) + "-"
                + (int) (Math.random() * 9000 + 1000);
    }

    private void applyShippingAddress(Order order, ShippingAddressDto a) {
        order.setShipName(a.name());
        order.setShipPhone(a.phone());
        order.setShipLine1(a.line1());
        order.setShipLine2(a.line2());
        order.setShipCity(a.city());
        order.setShipState(a.state());
        order.setShipPostalCode(a.postalCode());
        order.setShipCountry(a.country());
    }

    private TrackingResponse buildTracking(Order order) {
        // Cancelled / returned are terminal off-path states
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.RETURNED) {
            List<TrackingResponse.Step> steps = new ArrayList<>();
            steps.add(new TrackingResponse.Step("PENDING", "Order Placed", true, order.getCreatedAt()));
            steps.add(new TrackingResponse.Step(order.getStatus().name(),
                    order.getStatus() == OrderStatus.CANCELLED ? "Cancelled" : "Returned",
                    true, order.getUpdatedAt()));
            return new TrackingResponse(order.getOrderNumber(), order.getStatus().name(), steps);
        }

        OrderStatus[] path = {
                OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.PACKED,
                OrderStatus.SHIPPED, OrderStatus.DELIVERED
        };
        String[] labels = {"Order Placed", "Confirmed", "Packed", "Shipped", "Delivered"};
        int currentIdx = indexOf(path, order.getStatus());

        List<TrackingResponse.Step> steps = new ArrayList<>();
        for (int i = 0; i < path.length; i++) {
            boolean reached = i <= currentIdx;
            Instant at = i == 0 ? order.getCreatedAt()
                    : (i == currentIdx ? order.getUpdatedAt() : null);
            steps.add(new TrackingResponse.Step(path[i].name(), labels[i], reached, at));
        }
        return new TrackingResponse(order.getOrderNumber(), order.getStatus().name(), steps);
    }

    private int indexOf(OrderStatus[] path, OrderStatus status) {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == status) return i;
        }
        return 0;
    }
}

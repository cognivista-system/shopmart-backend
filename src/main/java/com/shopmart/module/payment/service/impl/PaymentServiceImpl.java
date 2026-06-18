package com.shopmart.module.payment.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.entity.PaymentStatus;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.payment.dto.*;
import com.shopmart.module.payment.entity.Payment;
import com.shopmart.module.payment.entity.Payment.PaymentState;
import com.shopmart.module.payment.gateway.PaymentGateway;
import com.shopmart.module.payment.mapper.PaymentMapper;
import com.shopmart.module.payment.repository.PaymentRepository;
import com.shopmart.module.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final String CURRENCY = "INR";

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway gateway;

    @Override
    @Transactional
    public PaymentIntentResponse initiate(Long userId, CreatePaymentRequest request) {
        Order order = ownedOrder(userId, request.orderId());
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("This order is already paid");
        }

        String method = order.getPaymentMethod();
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setMethod(method);
        payment.setAmount(order.getTotal());

        // Cash on delivery: no gateway round-trip. Confirm the order immediately.
        if ("COD".equals(method)) {
            payment.setProvider("none");
            payment.setStatus(PaymentState.PENDING);
            paymentRepository.save(payment);

            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            return new PaymentIntentResponse(payment.getId(), order.getId(), "none", null,
                    order.getTotal(), CURRENCY, payment.getStatus().name(), false);
        }

        // Gateway-backed methods: create a provider order and hand the ref to the client.
        String gatewayRef = gateway.createGatewayOrder(order.getId(), order.getTotal(), CURRENCY);
        payment.setProvider(gateway.provider());
        payment.setGatewayRef(gatewayRef);
        payment.setStatus(PaymentState.PENDING);
        paymentRepository.save(payment);

        return new PaymentIntentResponse(payment.getId(), order.getId(), gateway.provider(),
                gatewayRef, order.getTotal(), CURRENCY, payment.getStatus().name(), true);
    }

    @Override
    @Transactional
    public PaymentResponse verify(Long userId, VerifyPaymentRequest request) {
        Order order = ownedOrder(userId, request.orderId());
        Payment payment = paymentRepository.findByGatewayRef(request.gatewayRef())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "gatewayRef", request.gatewayRef()));

        if (!payment.getOrderId().equals(order.getId())) {
            throw new BadRequestException("Payment does not belong to this order");
        }

        boolean valid = gateway.verifySignature(request.gatewayRef(), request.transactionId(), request.signature());
        if (!valid) {
            payment.setStatus(PaymentState.FAILED);
            payment.setFailureReason("Signature verification failed");
            paymentRepository.save(payment);
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            throw new BadRequestException("Payment verification failed");
        }

        payment.setStatus(PaymentState.SUCCESS);
        payment.setTransactionId(request.transactionId());
        paymentRepository.save(payment);

        order.setPaymentStatus(PaymentStatus.PAID);
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        orderRepository.save(order);

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> history(Long userId, Long orderId) {
        ownedOrder(userId, orderId);
        return paymentRepository.findByOrderId(orderId).stream().map(PaymentMapper::toResponse).toList();
    }

    private Order ownedOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }
}

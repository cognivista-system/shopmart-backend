package com.shopmart.module.returns.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.entity.PaymentStatus;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.payment.entity.Payment;
import com.shopmart.module.payment.gateway.PaymentGateway;
import com.shopmart.module.payment.repository.PaymentRepository;
import com.shopmart.module.returns.dto.CreateReturnRequest;
import com.shopmart.module.returns.dto.ReturnDecisionRequest;
import com.shopmart.module.returns.dto.ReturnResponse;
import com.shopmart.module.returns.entity.ReturnRequest;
import com.shopmart.module.returns.entity.ReturnRequest.ReturnStatus;
import com.shopmart.module.returns.mapper.ReturnMapper;
import com.shopmart.module.returns.repository.ReturnRequestRepository;
import com.shopmart.module.returns.service.ReturnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private static final String CURRENCY = "INR";

    private final ReturnRequestRepository repository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway gateway;

    @Override
    @Transactional
    public ReturnResponse create(Long userId, CreateReturnRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.orderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.orderId()));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Only delivered orders can be returned");
        }
        boolean openOrDone = repository.existsByOrderIdAndStatusIn(order.getId(),
                List.of(ReturnStatus.REQUESTED, ReturnStatus.APPROVED, ReturnStatus.REFUNDED));
        if (openOrDone) {
            throw new BadRequestException("A return for this order is already in progress or completed");
        }

        ReturnRequest r = new ReturnRequest();
        r.setOrderId(order.getId());
        r.setUserId(userId);
        r.setReason(request.reason());
        r.setStatus(ReturnStatus.REQUESTED);
        r.setRefundAmount(order.getTotal());
        return ReturnMapper.toResponse(repository.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnResponse> listMine(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(ReturnMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponse getMine(Long userId, Long id) {
        return ReturnMapper.toResponse(ownReturn(userId, id));
    }

    @Override
    @Transactional
    public ReturnResponse cancelMine(Long userId, Long id) {
        ReturnRequest r = ownReturn(userId, id);
        if (r.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException("Only a pending return can be cancelled");
        }
        r.setStatus(ReturnStatus.CANCELLED);
        return ReturnMapper.toResponse(repository.save(r));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReturnResponse> listAll(String status) {
        List<ReturnRequest> list;
        if (status == null || status.isBlank()) {
            list = repository.findAll();
        } else {
            list = repository.findByStatusOrderByCreatedAtDesc(parse(status));
        }
        return list.stream().map(ReturnMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ReturnResponse decide(Long id, ReturnDecisionRequest request) {
        ReturnRequest r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ReturnRequest", "id", id));
        if (r.getStatus() != ReturnStatus.REQUESTED) {
            throw new BadRequestException("This return has already been processed");
        }
        r.setAdminNote(request.adminNote());

        if (!request.approve()) {
            r.setStatus(ReturnStatus.REJECTED);
            r.setProcessedAt(Instant.now());
            return ReturnMapper.toResponse(repository.save(r));
        }

        Order order = orderRepository.findById(r.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", r.getOrderId()));

        BigDecimal amount = request.refundAmount() != null ? request.refundAmount() : order.getTotal();
        r.setRefundAmount(amount);

        // Best-effort gateway refund against the successful payment(s) for this order.
        String refundId = null;
        for (Payment p : paymentRepository.findByOrderId(order.getId())) {
            if (p.getStatus() == Payment.PaymentState.SUCCESS) {
                String gid = gateway.refund(p.getTransactionId(), amount, CURRENCY);
                if (gid != null && refundId == null) refundId = gid;
                p.setStatus(Payment.PaymentState.REFUNDED);
                paymentRepository.save(p);
            }
        }
        r.setGatewayRefundId(refundId);
        r.setStatus(ReturnStatus.REFUNDED);
        r.setProcessedAt(Instant.now());

        order.setStatus(OrderStatus.RETURNED);
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        orderRepository.save(order);

        log.info("[RETURN] order={} refunded amount={} gatewayRefundId={}", order.getId(), amount, refundId);
        return ReturnMapper.toResponse(repository.save(r));
    }

    private ReturnRequest ownReturn(Long userId, Long id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ReturnRequest", "id", id));
    }

    private ReturnStatus parse(String status) {
        try {
            return ReturnStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid return status: " + status);
        }
    }
}

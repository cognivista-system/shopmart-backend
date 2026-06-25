package com.shopmart.module.admin.service.impl;

import com.shopmart.module.admin.dto.AdminDashboardResponse;
import com.shopmart.module.admin.service.AdminDashboardService;
import com.shopmart.module.order.entity.OrderStatus;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse dashboard() {
        BigDecimal revenue = orderRepository.totalRevenue();
        return new AdminDashboardResponse(
                productRepository.count(),
                productRepository.countByStatus(ProductStatus.PENDING_APPROVAL),
                productRepository.countByStatus(ProductStatus.ACTIVE),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                revenue != null ? revenue : BigDecimal.ZERO,
                orderRepository.countDistinctCustomers()
        );
    }
}

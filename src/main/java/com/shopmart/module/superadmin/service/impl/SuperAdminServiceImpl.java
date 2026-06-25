package com.shopmart.module.superadmin.service.impl;

import com.shopmart.common.exception.ConflictException;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.superadmin.dto.AdminCreatedResponse;
import com.shopmart.module.superadmin.dto.CreateAdminRequest;
import com.shopmart.module.superadmin.dto.SuperAdminDashboardResponse;
import com.shopmart.module.superadmin.service.SuperAdminService;
import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AdminCreatedResponse createAdmin(CreateAdminRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("A user with this email already exists");
        }
        User admin = new User();
        admin.setName(req.name());
        admin.setEmail(req.email());
        admin.setPhone(req.phone());
        admin.setPasswordHash(passwordEncoder.encode(req.password()));
        admin.setEmailVerified(true);
        admin.setEnabled(true);
        admin.addRole(Role.ROLE_ADMIN);
        admin.addRole(Role.ROLE_CUSTOMER);
        admin = userRepository.save(admin);
        return new AdminCreatedResponse(
                admin.getId(), admin.getName(), admin.getEmail(),
                admin.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
    }

    @Override
    @Transactional(readOnly = true)
    public SuperAdminDashboardResponse dashboard() {
        BigDecimal revenue = orderRepository.totalRevenue();
        return new SuperAdminDashboardResponse(
                userRepository.countByRole(Role.ROLE_ADMIN),
                productRepository.count(),
                productRepository.countByStatus(ProductStatus.PENDING_APPROVAL),
                orderRepository.count(),
                revenue != null ? revenue : BigDecimal.ZERO,
                orderRepository.countDistinctCustomers()
        );
    }
}

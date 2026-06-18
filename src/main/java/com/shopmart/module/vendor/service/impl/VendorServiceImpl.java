package com.shopmart.module.vendor.service.impl;

import com.shopmart.common.dto.PageResponse;
import com.shopmart.common.exception.BadRequestException;
import com.shopmart.common.exception.ConflictException;
import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.order.entity.OrderItem;
import com.shopmart.module.order.repository.OrderItemRepository;
import com.shopmart.module.product.mapper.ProductMapper;
import com.shopmart.module.product.repository.ProductRepository;
import com.shopmart.module.product.dto.ProductSummary;
import com.shopmart.module.product.dto.ProductRequest;
import com.shopmart.module.product.dto.ProductResponse;
import com.shopmart.module.product.entity.ProductStatus;
import com.shopmart.module.product.service.ProductService;
import com.shopmart.module.user.entity.Role;
import com.shopmart.module.user.entity.User;
import com.shopmart.module.user.repository.UserRepository;
import com.shopmart.module.vendor.dto.*;
import com.shopmart.module.vendor.entity.Payout;
import com.shopmart.module.vendor.entity.PayoutStatus;
import com.shopmart.module.vendor.entity.Vendor;
import com.shopmart.module.vendor.entity.VendorStatus;
import com.shopmart.module.vendor.mapper.VendorMapper;
import com.shopmart.module.vendor.repository.PayoutRepository;
import com.shopmart.module.vendor.repository.VendorRepository;
import com.shopmart.module.vendor.service.VendorService;
import com.shopmart.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final PayoutRepository payoutRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public VendorResponse register(Long userId, VendorRegistrationRequest request) {
        if (vendorRepository.existsByUserId(userId)) {
            throw new ConflictException("You already have a vendor account");
        }
        Vendor v = new Vendor();
        v.setUserId(userId);
        v.setStoreName(request.storeName());
        v.setSlug(uniqueSlug(SlugUtils.slugify(request.storeName())));
        v.setDescription(request.description());
        v.setLogoUrl(request.logoUrl());
        v.setContactEmail(request.contactEmail());
        v.setContactPhone(request.contactPhone());
        v.setStatus(VendorStatus.PENDING);
        Vendor saved = vendorRepository.save(v);

        // Grant the vendor role so the user can access vendor endpoints
        userRepository.findById(userId).ifPresent(u -> {
            u.getRoles().add(Role.ROLE_VENDOR);
            userRepository.save(u);
        });
        return VendorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getMyVendor(Long userId) {
        return VendorMapper.toResponse(requireVendorForUser(userId));
    }

    @Override
    @Transactional
    public VendorResponse updateProfile(Long userId, VendorUpdateRequest request) {
        Vendor v = requireVendorForUser(userId);
        v.setStoreName(request.storeName());
        v.setDescription(request.description());
        v.setLogoUrl(request.logoUrl());
        v.setContactEmail(request.contactEmail());
        v.setContactPhone(request.contactPhone());
        return VendorMapper.toResponse(vendorRepository.save(v));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductSummary> myProducts(Long userId, Pageable pageable) {
        Vendor v = requireVendorForUser(userId);
        return PageResponse.from(productRepository.findByVendorId(v.getId(), pageable)
                .map(ProductMapper::toSummary));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VendorOrderItemResponse> myOrders(Long userId, Pageable pageable) {
        Vendor v = requireVendorForUser(userId);
        Page<VendorOrderItemResponse> page = orderItemRepository.findByVendorId(v.getId(), pageable)
                .map(this::toOrderItemResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorEarningsResponse myEarnings(Long userId) {
        Vendor v = requireVendorForUser(userId);
        return computeEarnings(v);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PayoutResponse> myPayouts(Long userId, Pageable pageable) {
        Vendor v = requireVendorForUser(userId);
        return PageResponse.from(payoutRepository.findByVendorId(v.getId(), pageable)
                .map(VendorMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getBySlug(String slug) {
        Vendor v = vendorRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "slug", slug));
        if (v.getStatus() != VendorStatus.APPROVED) {
            throw new ResourceNotFoundException("Vendor", "slug", slug);
        }
        return VendorMapper.toResponse(v);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getById(Long id) {
        return VendorMapper.toResponse(requireVendor(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<VendorResponse> list(String status, Pageable pageable) {
        Page<Vendor> page = (status == null || status.isBlank())
                ? vendorRepository.findAll(pageable)
                : vendorRepository.findByStatus(parseStatus(status), pageable);
        return PageResponse.from(page.map(VendorMapper::toResponse));
    }

    @Override
    @Transactional
    public VendorResponse updateStatus(Long id, VendorStatusUpdateRequest request) {
        Vendor v = requireVendor(id);
        v.setStatus(parseStatus(request.status()));
        if (request.commissionRate() != null) {
            v.setCommissionRate(request.commissionRate());
        }
        return VendorMapper.toResponse(vendorRepository.save(v));
    }

    @Override
    @Transactional
    public PayoutResponse createPayout(Long vendorId, PayoutRequest request) {
        requireVendor(vendorId);
        Payout p = new Payout();
        p.setVendorId(vendorId);
        p.setAmount(request.amount());
        p.setStatus(PayoutStatus.PENDING);
        p.setPeriodStart(request.periodStart());
        p.setPeriodEnd(request.periodEnd());
        p.setNote(request.note());
        return VendorMapper.toResponse(payoutRepository.save(p));
    }

    @Override
    @Transactional
    public PayoutResponse markPayoutPaid(Long payoutId) {
        Payout p = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout", "id", payoutId));
        p.setStatus(PayoutStatus.PAID);
        p.setPaidAt(Instant.now());
        return VendorMapper.toResponse(payoutRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PayoutResponse> listPayouts(Long vendorId, Pageable pageable) {
        requireVendor(vendorId);
        return PageResponse.from(payoutRepository.findByVendorId(vendorId, pageable)
                .map(VendorMapper::toResponse));
    }

    // ---- helpers ----

    @Override
    @Transactional(readOnly = true)
    public VendorDashboardResponse myDashboard(Long userId) {
        Vendor v = requireVendorForUser(userId);
        VendorEarningsResponse e = computeEarnings(v);
        long total = productRepository.countByVendorId(v.getId());
        long active = productRepository.countByVendorIdAndStatus(v.getId(), ProductStatus.ACTIVE);
        long pending = productRepository.countByVendorIdAndStatus(v.getId(), ProductStatus.PENDING_APPROVAL);
        return new VendorDashboardResponse(v.getId(), v.getStoreName(), v.getStatus().name(),
                total, active, pending, e.grossSales(), e.commissionRate(), e.commission(),
                e.netEarnings(), e.totalPaidOut(), e.pendingBalance());
    }

    @Override
    @Transactional
    public ProductResponse createProduct(Long userId, ProductRequest request) {
        Vendor v = requireApprovedVendor(userId);
        return productService.createForVendor(v.getId(), request);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long userId, Long productId, ProductRequest request) {
        Vendor v = requireApprovedVendor(userId);
        return productService.updateForVendor(v.getId(), productId, request);
    }

    @Override
    @Transactional
    public VendorResponse updateCommission(Long id, BigDecimal commissionRate) {
        Vendor v = requireVendor(id);
        v.setCommissionRate(commissionRate);
        return VendorMapper.toResponse(vendorRepository.save(v));
    }

    private Vendor requireApprovedVendor(Long userId) {
        Vendor v = requireVendorForUser(userId);
        if (v.getStatus() != VendorStatus.APPROVED) {
            throw new BadRequestException("Your vendor account must be approved before managing products");
        }
        return v;
    }

    private VendorEarningsResponse computeEarnings(Vendor v) {
        BigDecimal gross = orderItemRepository.vendorGrossSales(v.getId());
        if (gross == null) gross = BigDecimal.ZERO;
        BigDecimal rate = v.getCommissionRate() != null ? v.getCommissionRate() : BigDecimal.ZERO;
        BigDecimal commission = gross.multiply(rate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal net = gross.subtract(commission);
        BigDecimal paidOut = payoutRepository.totalPaid(v.getId());
        if (paidOut == null) paidOut = BigDecimal.ZERO;
        BigDecimal pending = net.subtract(paidOut);
        return new VendorEarningsResponse(v.getId(), gross, rate, commission, net, paidOut, pending);
    }

    private VendorOrderItemResponse toOrderItemResponse(OrderItem oi) {
        return new VendorOrderItemResponse(
                oi.getId(), oi.getOrder().getId(), oi.getOrder().getOrderNumber(),
                oi.getProductId(), oi.getProductName(), oi.getQuantity(),
                oi.getUnitPrice(), oi.getLineTotal(), oi.getOrder().getStatus().name());
    }

    private Vendor requireVendor(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
    }

    private Vendor requireVendorForUser(Long userId) {
        return vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("You do not have a vendor account"));
    }

    private String uniqueSlug(String base) {
        if (base == null || base.isBlank()) base = "store";
        return vendorRepository.existsBySlug(base)
                ? base + "-" + UUID.randomUUID().toString().substring(0, 6)
                : base;
    }

    private VendorStatus parseStatus(String status) {
        try {
            return VendorStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid vendor status: " + status);
        }
    }
}

package com.shopmart.module.returns.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "return_requests", indexes = {
        @Index(name = "idx_returns_user", columnList = "user_id"),
        @Index(name = "idx_returns_order", columnList = "order_id")
})
public class ReturnRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status = ReturnStatus.REQUESTED;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "admin_note", length = 1000)
    private String adminNote;

    @Column(name = "gateway_refund_id")
    private String gatewayRefundId;

    @Column(name = "processed_at")
    private Instant processedAt;

    public enum ReturnStatus {
        REQUESTED, APPROVED, REJECTED, REFUNDED, CANCELLED
    }
}

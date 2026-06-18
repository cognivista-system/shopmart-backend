package com.shopmart.module.loyalty.entity;

import com.shopmart.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "loyalty_transactions", indexes = @Index(name = "idx_loyalty_txn_user", columnList = "user_id"))
public class LoyaltyTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Positive for earn/credit, negative for redeem/debit. */
    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnType type;

    @Column(name = "order_id")
    private Long orderId;

    @Column(length = 255)
    private String description;

    public enum TxnType {
        EARN, REDEEM, ADJUST
    }
}

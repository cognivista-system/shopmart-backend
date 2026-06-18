package com.shopmart.module.loyalty.service.impl;

import com.shopmart.common.exception.BadRequestException;
import com.shopmart.module.loyalty.dto.LoyaltyBalanceResponse;
import com.shopmart.module.loyalty.dto.LoyaltyTransactionResponse;
import com.shopmart.module.loyalty.dto.RedeemResponse;
import com.shopmart.module.loyalty.entity.LoyaltyAccount;
import com.shopmart.module.loyalty.entity.LoyaltyTransaction;
import com.shopmart.module.loyalty.entity.LoyaltyTransaction.TxnType;
import com.shopmart.module.loyalty.mapper.LoyaltyMapper;
import com.shopmart.module.loyalty.repository.LoyaltyAccountRepository;
import com.shopmart.module.loyalty.repository.LoyaltyTransactionRepository;
import com.shopmart.module.loyalty.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyAccountRepository accountRepository;
    private final LoyaltyTransactionRepository txnRepository;

    /** Spend this much (order currency) to earn 1 point. */
    @Value("${app.loyalty.amount-per-point:100}")
    private BigDecimal amountPerPoint;

    /** Monetary value of 1 point when redeemed. */
    @Value("${app.loyalty.point-value:1.0}")
    private BigDecimal pointValue;

    @Override
    @Transactional(readOnly = true)
    public LoyaltyBalanceResponse balance(Long userId) {
        int bal = accountRepository.findByUserId(userId).map(LoyaltyAccount::getBalance).orElse(0);
        return new LoyaltyBalanceResponse(bal, pointValue,
                pointValue.multiply(BigDecimal.valueOf(bal)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyTransactionResponse> history(Long userId) {
        return txnRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(LoyaltyMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public RedeemResponse redeem(Long userId, int points) {
        if (points <= 0) throw new BadRequestException("Points to redeem must be positive");
        LoyaltyAccount account = account(userId);
        if (account.getBalance() < points) {
            throw new BadRequestException("Insufficient loyalty points");
        }
        account.setBalance(account.getBalance() - points);
        accountRepository.save(account);
        record(userId, -points, TxnType.REDEEM, null, "Redeemed " + points + " points");
        BigDecimal value = pointValue.multiply(BigDecimal.valueOf(points)).setScale(2, RoundingMode.HALF_UP);
        return new RedeemResponse(points, value, account.getBalance());
    }

    @Override
    @Transactional
    public void earnForOrder(Long userId, Long orderId, BigDecimal orderTotal) {
        if (orderTotal == null || amountPerPoint.signum() <= 0) return;
        int earned = orderTotal.divide(amountPerPoint, 0, RoundingMode.DOWN).intValue();
        if (earned <= 0) return;
        LoyaltyAccount account = account(userId);
        account.setBalance(account.getBalance() + earned);
        accountRepository.save(account);
        record(userId, earned, TxnType.EARN, orderId, "Earned on order");
    }

    @Override
    @Transactional
    public LoyaltyTransactionResponse adjust(Long userId, int points, String reason) {
        LoyaltyAccount account = account(userId);
        int newBalance = account.getBalance() + points;
        if (newBalance < 0) throw new BadRequestException("Adjustment would make the balance negative");
        account.setBalance(newBalance);
        accountRepository.save(account);
        LoyaltyTransaction t = record(userId, points, TxnType.ADJUST, null,
                reason == null || reason.isBlank() ? "Manual adjustment" : reason);
        return LoyaltyMapper.toResponse(t);
    }

    private LoyaltyAccount account(Long userId) {
        return accountRepository.findByUserId(userId).orElseGet(() -> {
            LoyaltyAccount a = new LoyaltyAccount();
            a.setUserId(userId);
            a.setBalance(0);
            return accountRepository.save(a);
        });
    }

    private LoyaltyTransaction record(Long userId, int points, TxnType type, Long orderId, String desc) {
        LoyaltyTransaction t = new LoyaltyTransaction();
        t.setUserId(userId);
        t.setPoints(points);
        t.setType(type);
        t.setOrderId(orderId);
        t.setDescription(desc);
        return txnRepository.save(t);
    }
}

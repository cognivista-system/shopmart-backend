package com.shopmart.module.auth.repository;

import com.shopmart.module.auth.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findFirstByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            String email, OtpVerification.OtpPurpose purpose);
}

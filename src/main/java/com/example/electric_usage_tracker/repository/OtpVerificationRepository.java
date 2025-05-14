package com.example.electric_usage_tracker.repository;

import com.example.electric_usage_tracker.model.OtpVerification;
import com.example.electric_usage_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    List<OtpVerification> findByUserAndPurposeAndUsedFalseOrderByExpiryDateDesc(User user, String purpose);
    OtpVerification findByOtpAndUserAndPurposeAndUsedFalse(String otp, User user, String purpose);
}

package com.example.electric_usage_tracker.service;

import com.example.electric_usage_tracker.model.OtpVerification;
import com.example.electric_usage_tracker.model.User;
import com.example.electric_usage_tracker.repository.OtpVerificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    
    public static final String PURPOSE_PASSWORD_RESET = "PASSWORD_RESET";
    public static final String PURPOSE_EMAIL_VERIFICATION = "EMAIL_VERIFICATION";
    
    public OtpService(OtpVerificationRepository otpRepository) {
        this.otpRepository = otpRepository;
    }
    
    public OtpVerification createOtpForPasswordReset(User user) {
        // Invalidate any existing OTPs for this purpose
        List<OtpVerification> existingOtps = otpRepository.findByUserAndPurposeAndUsedFalseOrderByExpiryDateDesc(user, PURPOSE_PASSWORD_RESET);
        existingOtps.forEach(otp -> {
            otp.setUsed(true);
            otpRepository.save(otp);
        });
        
        // Create new OTP
        OtpVerification otp = new OtpVerification(user, PURPOSE_PASSWORD_RESET);
        otpRepository.save(otp);
        
        // Email functionality disabled
        System.out.println("Would have sent password reset OTP to " + user.getEmail());
        
        return otp;
    }
    
    public OtpVerification createOtpForEmailVerification(User user) {
        // Invalidate any existing OTPs for this purpose
        List<OtpVerification> existingOtps = otpRepository.findByUserAndPurposeAndUsedFalseOrderByExpiryDateDesc(user, PURPOSE_EMAIL_VERIFICATION);
        existingOtps.forEach(otp -> {
            otp.setUsed(true);
            otpRepository.save(otp);
        });
        
        // Create new OTP
        OtpVerification otp = new OtpVerification(user, PURPOSE_EMAIL_VERIFICATION);
        otpRepository.save(otp);
        
        // Email functionality disabled
        System.out.println("Would have sent email verification OTP to " + user.getEmail());
        
        return otp;
    }
    
    public boolean verifyOtp(User user, String otp, String purpose) {
        OtpVerification verification = otpRepository.findByOtpAndUserAndPurposeAndUsedFalse(otp, user, purpose);
        
        if (verification != null && verification.isValid(otp)) {
            // Mark as used
            verification.setUsed(true);
            otpRepository.save(verification);
            return true;
        }
        
        return false;
    }
    
    public void invalidateUserOtps(User user, String purpose) {
        List<OtpVerification> otps = otpRepository.findByUserAndPurposeAndUsedFalseOrderByExpiryDateDesc(user, purpose);
        otps.forEach(otp -> {
            otp.setUsed(true);
            otpRepository.save(otp);
        });
    }
}

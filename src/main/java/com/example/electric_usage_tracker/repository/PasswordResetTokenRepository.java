package com.example.electric_usage_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.electric_usage_tracker.model.PasswordResetToken;
import com.example.electric_usage_tracker.model.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    PasswordResetToken findByToken(String token);
    
    PasswordResetToken findByUser(User user);
    
    void deleteByUser(User user);
}

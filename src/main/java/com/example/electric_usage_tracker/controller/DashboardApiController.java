package com.example.electric_usage_tracker.controller;

import com.example.electric_usage_tracker.model.Appliance;
import com.example.electric_usage_tracker.model.ApplianceUsage;
import com.example.electric_usage_tracker.model.CustomUserDetails;
import com.example.electric_usage_tracker.model.User;
import com.example.electric_usage_tracker.repository.UserRepository;
import com.example.electric_usage_tracker.service.ApplianceService;
import com.example.electric_usage_tracker.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final UsageService usageService;
    private final ApplianceService applianceService;
    private final UserRepository userRepository;

    public DashboardApiController(UsageService usageService, ApplianceService applianceService, UserRepository userRepository) {
        this.usageService = usageService;
        this.applianceService = applianceService;
        this.userRepository = userRepository;
    }

    /**
     * Get comprehensive dashboard data for charts and widgets
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getDashboardData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userDetails.getUser();
        Map<String, Object> response = new HashMap<>();

        // Add summary data
        response.put("totalEnergy", usageService.getTotalEnergy(user));
        response.put("totalCost", usageService.getTotalCost(user));
        response.put("recordCount", usageService.getAllUsages(user).size());

        // Add analytics data
        response.put("deviceDistribution", usageService.getDeviceDistribution(user));
        response.put("energyTrend", usageService.getEnergyTrend(user, 14));
        response.put("efficiencyScore", usageService.calculateEfficiencyScore(user));
        response.put("energyChange", usageService.getEnergyChangePercentage(user));
        response.put("costChange", usageService.getCostChangePercentage(user));

        return ResponseEntity.ok(response);
    }

    /**
     * Get list of common appliances for the calculator
     */
    @GetMapping("/appliances")
    public ResponseEntity<List<Appliance>> getAppliances() {
        return ResponseEntity.ok(applianceService.getAllAppliances());
    }

    /**
     * Save a usage calculation to the database
     */
    @PostMapping("/saveUsage")
    public ResponseEntity<Map<String, Object>> saveUsage(
            @RequestBody ApplianceUsage usage,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userDetails.getUser();
        usage.setUser(user);
        usage.setRecordedAt(LocalDateTime.now());
        usageService.saveUsage(usage);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", usage.getId());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a usage record
     */
    @DeleteMapping("/usage/{id}")
    public ResponseEntity<Map<String, Object>> deleteUsage(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userDetails.getUser();
        ApplianceUsage usage = usageService.getUsageById(id);
        
        Map<String, Object> response = new HashMap<>();
        
        if (usage != null && usage.getUser().getId().equals(user.getId())) {
            usageService.deleteUsage(id);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Usage record not found or access denied");
            return ResponseEntity.status(403).body(response);
        }
    }
    
    /**
     * Update user settings (name, default cost, preferred currency)
     */
    @PostMapping("/saveSettings")
    public ResponseEntity<Map<String, Object>> saveSettings(
            @RequestParam("fullName") String fullName,
            @RequestParam("defaultCost") String defaultCost,
            @RequestParam("currency") String currency,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        User user = userDetails.getUser();
        // Update user details
        user.setName(fullName);
        user.setDefaultCost(Double.parseDouble(defaultCost));
        user.setPreferredCurrency(currency);
        
        // Save updated user
        userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Settings updated successfully");
        
        return ResponseEntity.ok(response);
    }
}

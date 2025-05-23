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
import java.util.LinkedHashMap;
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

        // Get today's date for fresh data check
        LocalDateTime now = LocalDateTime.now();
        String today = now.toLocalDate().toString();
        
        // Add summary data - always include all records regardless of date
        response.put("totalEnergy", usageService.getTotalEnergy(user));
        response.put("totalCost", usageService.getTotalCost(user));
        response.put("recordCount", usageService.getAllUsages(user).size());

        // Get all usage records to ensure we include today's data
        List<ApplianceUsage> allUsages = usageService.getAllUsages(user);
        
        // Force refresh device distribution with all usage data including today
        Map<String, Double> distribution = new HashMap<>();
        for (ApplianceUsage usage : allUsages) {
            String applianceName = usage.getApplianceName();
            double kWhConsumed = usage.getKWhConsumed();
            distribution.put(applianceName, 
                    distribution.getOrDefault(applianceName, 0.0) + kWhConsumed);
        }
        response.put("deviceDistribution", distribution);
        
        // Force recalculation of energy trend to include today
        Map<String, Double> trend = new LinkedHashMap<>(); // Preserve insertion order
        
        // Setup last 14 days
        for (int i = 13; i >= 0; i--) {
            String dateStr = now.toLocalDate().minusDays(i).toString();
            trend.put(dateStr, 0.0);
        }
        
        // Fill with ALL usage data, including today
        for (ApplianceUsage usage : allUsages) {
            String dateStr = usage.getRecordedAt().toLocalDate().toString();
            if (trend.containsKey(dateStr)) {
                trend.put(dateStr, trend.get(dateStr) + usage.getKWhConsumed());
            }
        }
        response.put("energyTrend", trend);
        
        // Other analytics data
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

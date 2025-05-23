package com.example.electric_usage_tracker.service;

import com.example.electric_usage_tracker.model.ApplianceUsage;
import com.example.electric_usage_tracker.model.User;
import com.example.electric_usage_tracker.repository.ApplianceUsageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsageService {

    private final ApplianceUsageRepository applianceUsageRepository;

    public UsageService(ApplianceUsageRepository applianceUsageRepository) {
        this.applianceUsageRepository = applianceUsageRepository;
    }

    public List<ApplianceUsage> getAllUsages(User user) {
        return applianceUsageRepository.findByUserOrderByRecordedAtDesc(user);
    }

    public Double getTotalEnergy(User user) {
        Double total = applianceUsageRepository.getTotalEnergyByUser(user);
        return total != null ? total : 0.0;
    }

    public Double getTotalCost(User user) {
        Double total = applianceUsageRepository.getTotalCostByUser(user);
        return total != null ? total : 0.0;
    }
    
    /**
     * Get device usage distribution data for visualization
     */
    public Map<String, Double> getDeviceDistribution(User user) {
        List<ApplianceUsage> usages = getAllUsages(user);
        Map<String, Double> distribution = new HashMap<>();
        
        // Group by appliance name and sum kWh consumed
        for (ApplianceUsage usage : usages) {
            String applianceName = usage.getApplianceName();
            double kWhConsumed = usage.getKWhConsumed();
            
            distribution.put(applianceName, 
                    distribution.getOrDefault(applianceName, 0.0) + kWhConsumed);
        }
        
        return distribution;
    }
    
    /**
     * Get energy consumption trend for the last X days
     */
    public Map<String, Double> getEnergyTrend(User user, int days) {
        List<ApplianceUsage> usages = getAllUsages(user);
        Map<String, Double> trend = new LinkedHashMap<>(); // Preserve insertion order
        
        // Get current date and X days ago
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days);
        
        // Initialize map with all dates in range (including zeros)
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            trend.put(date.format(DateTimeFormatter.ISO_LOCAL_DATE), 0.0);
        }
        
        // Fill in actual consumption data
        for (ApplianceUsage usage : usages) {
            LocalDate usageDate = usage.getRecordedAt().toLocalDate();
            if ((usageDate.isEqual(startDate) || usageDate.isAfter(startDate)) && (usageDate.isEqual(today) || usageDate.isBefore(today))) {
                String dateKey = usageDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                if (trend.containsKey(dateKey)) {
                    trend.put(dateKey, trend.get(dateKey) + usage.getKWhConsumed());
                }
            }
        }
        
        return trend;
    }
    
    /**
     * Calculate user's efficiency score based on usage patterns
     * Score range: 0-100 (higher is better)
     */
    public int calculateEfficiencyScore(User user) {
        Double totalEnergy = getTotalEnergy(user);
        List<ApplianceUsage> usages = getAllUsages(user);
        
        if (usages.isEmpty() || totalEnergy == 0) {
            return 0; // No data yet
        }
        
        // Base score starts at 100
        int score = 100;
        
        // Calculate average daily consumption in last month
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);
        
        double recentConsumption = 0.0;
        int daysWithUsage = 0;
        
        Map<LocalDate, Double> dailyConsumption = new HashMap<>();
        
        for (ApplianceUsage usage : usages) {
            LocalDate usageDate = usage.getRecordedAt().toLocalDate();
            if (usageDate.isAfter(monthAgo)) {
                // Add to daily consumption map
                dailyConsumption.put(usageDate, 
                        dailyConsumption.getOrDefault(usageDate, 0.0) + usage.getKWhConsumed());
                recentConsumption += usage.getKWhConsumed();
            }
        }
        
        daysWithUsage = dailyConsumption.size();
        double avgDailyConsumption = daysWithUsage > 0 ? recentConsumption / daysWithUsage : 0;
        
        // Typical household uses ~30 kWh per day
        // Deduct points for high consumption
        if (avgDailyConsumption > 30) {
            score -= Math.min(40, (int)((avgDailyConsumption - 30) * 2)); // Max 40 point penalty
        }
        
        // Bonus for consistent tracking (regular entries)
        if (daysWithUsage > 20) {
            score += 10; // Bonus for consistent tracking
        } else if (daysWithUsage > 10) {
            score += 5;
        }
        
        // Prevent score from going out of bounds
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Calculate energy usage change percentage compared to previous period
     */
    public double getEnergyChangePercentage(User user) {
        List<ApplianceUsage> usages = getAllUsages(user);
        
        if (usages.isEmpty()) {
            return 0.0;
        }
        
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksAgo = today.minusDays(14);
        LocalDate fourWeeksAgo = today.minusDays(28);
        
        double currentPeriodUsage = 0.0;
        double previousPeriodUsage = 0.0;
        
        for (ApplianceUsage usage : usages) {
            LocalDate usageDate = usage.getRecordedAt().toLocalDate();
            
            if ((usageDate.isAfter(twoWeeksAgo) || usageDate.isEqual(twoWeeksAgo)) && (usageDate.isEqual(today) || usageDate.isBefore(today))) {
                currentPeriodUsage += usage.getKWhConsumed();
            } else if ((usageDate.isAfter(fourWeeksAgo) || usageDate.isEqual(fourWeeksAgo)) && usageDate.isBefore(twoWeeksAgo)) {
                previousPeriodUsage += usage.getKWhConsumed();
            }
        }
        
        if (previousPeriodUsage == 0) {
            return currentPeriodUsage > 0 ? 100.0 : 0.0; // If no previous usage, consider it 100% increase if there is current usage
        }
        
        return ((currentPeriodUsage - previousPeriodUsage) / previousPeriodUsage) * 100.0;
    }
    
    /**
     * Calculate cost change percentage compared to previous period
     */
    public double getCostChangePercentage(User user) {
        // Since cost is directly proportional to energy in our model, 
        // we can use the same percentage
        return getEnergyChangePercentage(user);
    }

    public void saveUsage(ApplianceUsage usage) {
        usage.recalculate();
        applianceUsageRepository.save(usage);
    }

    public ApplianceUsage getUsageById(Long id) {
        return applianceUsageRepository.findById(id).orElse(null);
    }

    public void deleteUsage(Long id) {
        applianceUsageRepository.deleteById(id);
    }

    public void deleteAllUsages() {
        applianceUsageRepository.deleteAll();
    }
}
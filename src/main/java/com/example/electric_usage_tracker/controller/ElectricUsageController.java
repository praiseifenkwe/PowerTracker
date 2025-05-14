    package com.example.electric_usage_tracker.controller;

import com.example.electric_usage_tracker.model.ApplianceUsage;
import com.example.electric_usage_tracker.model.CustomUserDetails;
import com.example.electric_usage_tracker.model.User;
import com.example.electric_usage_tracker.service.ApplianceService;
import com.example.electric_usage_tracker.service.UsageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class ElectricUsageController {

    private final UsageService usageService;
    private final ApplianceService applianceService;

    public ElectricUsageController(UsageService usageService, ApplianceService applianceService) {
        this.usageService = usageService;
        this.applianceService = applianceService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            // Determine display name: use name if available, else fall back to username (email)
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        
            User user = userDetails.getUser();
            model.addAttribute("usage", new ApplianceUsage());
            model.addAttribute("appliances", applianceService.getAllAppliances());
            model.addAttribute("usageHistory", usageService.getAllUsages(user));
            model.addAttribute("totalEnergy", usageService.getTotalEnergy(user));
            model.addAttribute("totalCost", usageService.getTotalCost(user));
            model.addAttribute("recordCount", usageService.getAllUsages(user).size());
        }
        return "homepage";
    }
    
    /**
     * Add common attributes to the model for all dashboard pages
     */
    private void addCommonDashboardAttributes(Model model, CustomUserDetails userDetails) {
        if (userDetails != null) {
            // Determine display name: use name if available, else fall back to username (email)
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        
            User user = userDetails.getUser();
            model.addAttribute("usage", new ApplianceUsage());
            model.addAttribute("appliances", applianceService.getAllAppliances());
            model.addAttribute("usageHistory", usageService.getAllUsages(user));
            model.addAttribute("totalEnergy", usageService.getTotalEnergy(user));
            model.addAttribute("totalCost", usageService.getTotalCost(user));
            model.addAttribute("recordCount", usageService.getAllUsages(user).size());
            
            // Add analytics data for device usage distribution
            Map<String, Double> deviceDistribution = usageService.getDeviceDistribution(user);
            model.addAttribute("deviceDistribution", deviceDistribution);
            
            // Add energy consumption trend data (last 2 weeks)
            Map<String, Double> energyTrend = usageService.getEnergyTrend(user, 14);
            model.addAttribute("energyTrend", energyTrend);
            
            // Add efficiency score
            model.addAttribute("efficiencyScore", usageService.calculateEfficiencyScore(user));
            
            // Add energy change percentage compared to previous period
            model.addAttribute("energyChange", usageService.getEnergyChangePercentage(user));
            model.addAttribute("costChange", usageService.getCostChangePercentage(user));
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "dashboard");
        return "dashboard";
    }
    
    @GetMapping("/analytics")
    public String analytics(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "analytics");
        return "dashboard";
    }
    
    @GetMapping("/usage-calculator")
    public String usageCalculator(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "calculator");
        return "dashboard";
    }
    
    @GetMapping("/management")
    public String management(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "management");
        return "dashboard";
    }
    
    @GetMapping("/usage-history")
    public String usageHistory(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "history");
        return "dashboard";
    }
    
    @GetMapping("/settings")
    public String settings(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        addCommonDashboardAttributes(model, userDetails);
        model.addAttribute("activeSection", "settings");
        return "dashboard";
    }
    
    @GetMapping("/about")
    public String about(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        }
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        }
        return "contact";
    }
    
    @GetMapping("/privacy-policy")
    public String privacyPolicy(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        }
        return "privacy-policy";
    }
    
    @GetMapping("/terms-of-service")
    public String termsOfService(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        }
        return "terms-of-service";
    }
    
    @GetMapping("/cookie-policy")
    public String cookiePolicy(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String displayName = userDetails.getName() != null && !userDetails.getName().isEmpty() 
                ? userDetails.getName() 
                : userDetails.getUsername();
            model.addAttribute("displayName", displayName);
        }
        return "cookie-policy";
    }

    @PostMapping("/calculate")
    public String calculateUsage(@ModelAttribute ApplianceUsage usage,
                                @RequestParam(required = false) String selectedAppliance,
                                @RequestParam(required = false) String customAppliance,
                                @RequestParam String timeUnit,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        if ("custom".equals(selectedAppliance)) {
            usage.setApplianceName(customAppliance);
        } else if (selectedAppliance != null && !selectedAppliance.isEmpty()) {
            usage.setApplianceName(selectedAppliance);
            var appliance = applianceService.getApplianceByName(selectedAppliance);
            if (appliance != null) {
                usage.setWattage(appliance.getWattage());
            }
        }
        usage.setTimeUnit(timeUnit);
        usage.setUser(user);
        usageService.saveUsage(usage);
        return "redirect:/usage-calculator";
    }

    @PostMapping("/delete/{id}")
    public String deleteUsage(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        ApplianceUsage usage = usageService.getUsageById(id);
        if (usage != null && usage.getUser().getId().equals(user.getId())) {
            usageService.deleteUsage(id);
        }
        return "redirect:/usage-history";
    }

    @PostMapping("/delete-all")
    public String deleteAllUsages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<ApplianceUsage> usages = usageService.getAllUsages(user);
        for (ApplianceUsage usage : usages) {
            usageService.deleteUsage(usage.getId());
        }
        return "redirect:/usage-history";
    }
}
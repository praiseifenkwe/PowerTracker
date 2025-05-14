package com.example.electric_usage_tracker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.electric_usage_tracker.model.Appliance;

@Service
public class ApplianceService {
    
    private List<Appliance> commonAppliances;
    
    public ApplianceService() {
        // Initialize common appliances with their typical wattage
        commonAppliances = new ArrayList<>();
        commonAppliances.add(new Appliance("Refrigerator", 150));
        commonAppliances.add(new Appliance("Air Conditioner", 1500));
        commonAppliances.add(new Appliance("Television", 100));
        commonAppliances.add(new Appliance("Washing Machine", 500));
        commonAppliances.add(new Appliance("Microwave Oven", 1000));
        commonAppliances.add(new Appliance("Electric Iron", 1100));
        commonAppliances.add(new Appliance("Desktop Computer", 200));
        commonAppliances.add(new Appliance("Laptop", 50));
        commonAppliances.add(new Appliance("Light Bulb (LED)", 10));
        commonAppliances.add(new Appliance("Light Bulb (Incandescent)", 60));
        commonAppliances.add(new Appliance("Electric Fan", 70));
        commonAppliances.add(new Appliance("Water Heater", 4000));
        commonAppliances.add(new Appliance("Toaster", 850));
        commonAppliances.add(new Appliance("Coffee Maker", 800));
        commonAppliances.add(new Appliance("Vacuum Cleaner", 1400));
        commonAppliances.add(new Appliance("Smartphone Charger", 5));
    }
    
    public List<Appliance> getAllAppliances() {
        return commonAppliances;
    }
    
    public Appliance getApplianceByName(String name) {
        return commonAppliances.stream()
                .filter(appliance -> appliance.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
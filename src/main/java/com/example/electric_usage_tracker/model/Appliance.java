package com.example.electric_usage_tracker.model;

public class Appliance {
    private String name;
    private double wattage;

    public Appliance(String name, double wattage) {
        this.name = name;
        this.wattage = wattage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWattage() {
        return wattage;
    }

    public void setWattage(double wattage) {
        this.wattage = wattage;
    }
}
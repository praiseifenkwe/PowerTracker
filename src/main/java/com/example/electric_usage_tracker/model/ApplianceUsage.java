package com.example.electric_usage_tracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ApplianceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String applianceName;
    private double wattage;
    private double hoursUsed;
    private String timeUnit;
    private double kWhConsumed;
    private double cost;
    private LocalDateTime recordedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ApplianceUsage() {
        this.recordedAt = LocalDateTime.now();
        this.timeUnit = "hours";
    }

    public ApplianceUsage(String applianceName, double wattage, double hoursUsed, String timeUnit, User user) {
        this.applianceName = applianceName;
        this.wattage = wattage;
        this.hoursUsed = hoursUsed;
        this.timeUnit = timeUnit != null ? timeUnit : "hours";
        this.recordedAt = LocalDateTime.now();
        this.user = user;
        recalculate();
    }

    // Existing getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplianceName() { return applianceName; }
    public void setApplianceName(String applianceName) { this.applianceName = applianceName; }
    public double getWattage() { return wattage; }
    public void setWattage(double wattage) { this.wattage = wattage; }
    public double getHoursUsed() { return hoursUsed; }
    public void setHoursUsed(double hoursUsed) { this.hoursUsed = hoursUsed; }
    public String getTimeUnit() { return timeUnit; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    public double getKWhConsumed() { return kWhConsumed; }
    public void setKWhConsumed(double kWhConsumed) { this.kWhConsumed = kWhConsumed; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public void recalculate() {
        double hours;
        switch (this.timeUnit != null ? this.timeUnit : "hours") {
            case "days": hours = this.hoursUsed * 24; break;
            case "weeks": hours = this.hoursUsed * 24 * 7; break;
            case "months": hours = this.hoursUsed * 24 * 30; break;
            default: hours = this.hoursUsed; break;
        }
        this.kWhConsumed = (this.wattage * hours) / 1000;
        this.cost = this.kWhConsumed * 70;
    }
}
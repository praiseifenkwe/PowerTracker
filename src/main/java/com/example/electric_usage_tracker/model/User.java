package com.example.electric_usage_tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password; // Will store encoded password
    private String name;     // Added for display purposes
    
    private String imageUrl; // Profile picture URL from OAuth providers
    
    private String provider; // OAuth provider (google, facebook, github)
    private String providerId; // ID from the OAuth provider
    
    @Column(columnDefinition = "boolean default false")
    private boolean emailVerified = false;
    
    private Double defaultCost = 0.12;
    
    private String preferredCurrency = "NGN";

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public Double getDefaultCost() { return defaultCost; }
    public void setDefaultCost(Double defaultCost) { this.defaultCost = defaultCost; }
    
    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }
}
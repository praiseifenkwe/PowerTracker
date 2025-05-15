# PowerTracker Application: Detailed Technical Guide for Team Members

## Introduction to PowerTracker

PowerTracker is a web-based application designed to help users monitor and optimize their electricity consumption. The project was built using Spring Boot, Thymeleaf, and modern frontend technologies to create an intuitive and interactive experience for tracking appliance energy usage.

## System Architecture

### Three-Tier Architecture
PowerTracker implements a classic three-tier architecture:
1. **Presentation Layer**: Thymeleaf templates with Tailwind CSS and JavaScript
2. **Application Layer**: Spring Boot controllers and services
3. **Data Layer**: PostgreSQL database accessed via Spring Data JPA

### Key Components

#### Backend Components
1. **Models**:
   - `User.java`: Stores user information, authentication details, and preferences (email, password, default cost settings)
   - `ApplianceUsage.java`: Core data model that tracks individual usage entries and calculates electricity consumption
   - `Appliance.java`: Simple class representing appliance types with name and wattage properties
   - `OtpVerification.java`: Handles email verification tokens
   - `PasswordResetToken.java`: Manages password reset functionality

2. **Controllers**:
   - `ElectricUsageController.java`: Primary controller handling dashboard views and user interaction
   - `AuthController.java`: Manages user authentication flows
   - `DashboardApiController.java`: Provides REST endpoints for dashboard data updates

3. **Services**:
   - `UsageService.java`: Business logic for calculating energy consumption, costs, and analytics
   - `ApplianceService.java`: Manages the appliance catalog
   - `UserService.java`: Handles user management operations
   - `OtpService.java`: Manages email verification

4. **Repositories**:
   - JPA repositories that provide database access for each model entity

#### Frontend Components
1. **Templates**: Thymeleaf templates for server-side rendering
   - `dashboard.html`: Main application interface with multiple sections
   - `homepage.html`: Landing page for non-authenticated users
   - Authentication pages (login.html, signup.html)

2. **JavaScript**:
   - Chart visualization code
   - Form validation and submission
   - Dynamic UI updates
   - Energy calculation functions

3. **CSS Framework**:
   - Tailwind CSS for responsive design
   - Custom CSS for specialized UI components

## Application Workflow

### 1. Authentication Flow
- Registration: Users register via form 
- Login: Credentials or social login

### 2. Energy Usage Tracking
When a user records an appliance's energy usage:
1. User selects an appliance and specifies wattage
2. User enters usage duration and time unit (hours, days, weeks, months)
3. Application calculates kWh consumption using formula: `(wattage * hours) / 1000`
4. Cost is calculated based on user's default electricity rate
5. Results are stored in the database and reflected in analytics

### 3. Data Analysis Pipeline
The application processes usage data to generate:
- Total energy consumption
- Distribution of energy usage by device
- Historical trends
- Efficiency scores
- Cost projections

## Key Formulas and Calculations

### Energy Calculation
```
kWh = (wattage * hours) / 1000
```

### Time Unit Conversion
```
switch (timeUnit) {
    case "days": hours = hoursUsed * 24; break;
    case "weeks": hours = hoursUsed * 24 * 7; break;
    case "months": hours = hoursUsed * 24 * 30; break;
    default: hours = hoursUsed; break;
}
```

### Cost Calculation
```
cost = kWhConsumed * electricityRate
```

### Efficiency Score Algorithm
The efficiency score is calculated based on:
- Average daily consumption
- Consistency of tracking
- Comparison to typical household usage (30 kWh benchmark)

## Database Schema

### Users Table
- id (PK)
- email (unique)
- password (encrypted)
- name
- imageUrl
- provider (OAuth provider)
- providerId
- emailVerified
- defaultCost
- preferredCurrency

### ApplianceUsage Table
- id (PK)
- applianceName
- wattage
- hoursUsed
- timeUnit
- kWhConsumed (calculated)
- cost (calculated)
- recordedAt (timestamp)
- user_id (FK to Users)

## Security Implementation

### Authentication Security
- Spring Security for authentication management
- Password encryption using BCrypt
- OAuth2 integration for social login
- CSRF protection on all forms

### Data Protection
- Input validation on all user inputs
- Parameterized queries to prevent SQL injection
- User data isolation (users can only see their own data)

## Technical Challenges and Solutions

### Challenge 1: Accurate Energy Calculation
**Problem**: Different appliances have varying wattages and usage patterns
**Solution**: Created a flexible calculation system that allows for custom wattage input and different time units

### Challenge 2: Data Visualization
**Problem**: Needed intuitive visualizations for complex energy data
**Solution**: Implemented Chart.js and ApexCharts for responsive, interactive charts

### Challenge 3: Authentication Options
**Problem**: Users expect multiple login options
**Solution**: Implemented OAuth2 with multiple providers while maintaining traditional login

## Code Walkthrough

### Example: Usage Calculation Logic
```java
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
```

### Example: Efficiency Score Calculation
```java
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
```

## Testing the Application

### Manual Testing Checklist
1. Dashboard Functionality
   - Check all metrics display correctly
   - Test chart interactions
   - Verify efficiency score calculation

2. Usage Calculator
   - Test with predefined appliances
   - Test with custom appliance entry
   - Verify calculations for different time units
   - Check saved results appear in history

3. Settings
   - Change electricity rate
   - Update currency preference
   - Verify changes reflect in calculations

### Common Bugs and Fixes
- **Issue**: Energy calculations showing incorrect values
  **Fix**: Double-check time unit conversion logic

- **Issue**: Charts not displaying on some browsers
  **Fix**: Ensure Chart.js is properly initialized after DOM is loaded

- **Issue**: Users unable to delete history items
  **Fix**: Verify CSRF token is properly included in delete requests

## Deployment Instructions
1. Ensure Java 17 and Maven are installed
2. Configure database properties in application.properties
3. Build the application with `mvn clean package`
4. Run with `java -jar target/electric-usage-tracker-0.0.1-SNAPSHOT.jar`
5. For Docker deployment, use the provided Dockerfile

## Future Development Roadmap
1. Mobile application integration
2. Smart meter direct input
3. AI-powered usage predictions
4. Community comparison features
5. API for third-party integrations

## How to Contribute
1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add tests for new functionality
5. Submit a pull request

## Troubleshooting Guide
- **Issue**: Application fails to start
  **Solution**: Check database connection and configuration

- **Issue**: Charts not loading
  **Solution**: Verify browser console for JavaScript errors

- **Issue**: Calculation results seem wrong
  **Solution**: Validate input values and verify formula implementation

---

This technical guide should help team members understand the full architecture and implementation details of the PowerTracker application. For specific code questions or implementation details, please refer to the codebase documentation comments.

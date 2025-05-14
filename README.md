# Electric Usage Tracker

A premium web application designed to track and manage household electricity usage with modern UI and comprehensive features.

![Electric Usage Tracker](https://i.imgur.com/placeholder-image.jpg)

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Database Schema](#database-schema)
- [Security Features](#security-features)
- [Installation and Setup](#installation-and-setup)
- [Deployment Guide](#deployment-guide)
- [Future Enhancements](#future-enhancements)
- [Presentation Guide](#presentation-guide)

## Overview

The Electric Usage Tracker is a comprehensive web application designed to help users monitor, analyze, and optimize their electricity consumption. The application features a premium dark-themed UI with glass-card effects, responsive design, and intuitive navigation. Users can track their electricity usage, view historical data, calculate costs, and receive tips on reducing consumption.

### Target Audience
- Homeowners looking to reduce energy costs
- Environmentally conscious individuals tracking their carbon footprint
- Property managers tracking electricity usage across multiple properties
- Students or educators demonstrating energy consumption patterns

## Features

### Core Functionality
- **Usage Tracking**: Log electricity readings with timestamps
- **Cost Calculation**: Calculate electricity costs based on current tariffs
- **Usage Visualization**: View usage trends and patterns through graphs
- **Bill Estimation**: Estimate upcoming bills based on current usage
- **User Authentication**: Secure login and registration system
- **Responsive Design**: Works across desktop, tablet, and mobile devices

### User Experience
- **Premium Dark Theme**: Consistent dark color scheme with indigo/purple accents
- **Glass-card Effects**: Modern UI with glass morphism design elements
- **Gradient Text**: Visually appealing gradient effects for headers
- **Floating Labels**: Intuitive form inputs with floating label animations
- **Multi-page Layout**: Organized into Home, Dashboard, About, and Contact pages

## Technology Stack

### Backend
- **Java 17+**: Main programming language
- **Spring Boot**: Framework for creating stand-alone, production-grade applications
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access abstraction
- **Hibernate**: ORM (Object-Relational Mapping)
- **PostgreSQL**: Relational database management system

### Frontend
- **Thymeleaf**: Server-side Java template engine
- **HTML5/CSS3**: Structure and styling
- **JavaScript**: Client-side interactivity
- **Bootstrap**: Responsive design framework
- **Chart.js**: Interactive data visualization

### Development Tools
- **Maven**: Dependency management and build
- **Git**: Version control
- **Spring DevTools**: Development productivity tools
- **Render**: Deployment platform

## Project Structure

The application follows a standard Spring Boot MVC architecture:

```
electric-usage-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── electric_usage_tracker/
│   │   │               ├── config/            # Configuration classes
│   │   │               ├── controller/        # MVC controllers
│   │   │               ├── model/             # Entity classes
│   │   │               ├── repository/        # Database repositories
│   │   │               ├── service/           # Business logic
│   │   │               └── ElectricUsageTrackerApplication.java
│   │   └── resources/
│   │       ├── static/                        # Static resources (CSS, JS, images)
│   │       ├── templates/                     # Thymeleaf templates
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/                              # Test classes
├── pom.xml                                   # Maven configuration
└── README.md                                 # Project documentation
```

### Key Components

#### Controllers
- **ElectricUsageController**: Main controller handling dashboard and calculator functions
- **AuthController**: Handles user authentication (login/signup)
- **HomeController**: Controls public pages (home, about, contact)

#### Models
- **User**: User account information
- **UsageRecord**: Electricity consumption records
- **Tariff**: Electricity pricing information
- **OtpVerification**: One-time password verification (for account security)
- **CustomUserDetails**: Extended user details for authentication

#### Services
- **UserService**: User management and authentication
- **UsageService**: Processing of electricity consumption data
- **TariffService**: Management of electricity pricing information
- **OtpService**: Handling one-time passwords

#### Repositories
- **UserRepository**: Data access for user information
- **UsageRecordRepository**: Data access for usage records
- **TariffRepository**: Data access for tariff information
- **OtpVerificationRepository**: Data access for OTP verification

#### Configuration
- **SecurityConfig**: Spring Security configuration
- **WebMvcConfig**: Spring MVC configuration

## How It Works

### User Journey

1. **Registration & Login**:
   - Users register with email and password
   - Authentication system verifies credentials
   - Session management maintains user state

2. **Dashboard Experience**:
   - Upon login, users are directed to their personalized dashboard
   - Dashboard displays usage summary, recent entries, and visualizations
   - Navigation allows access to various tools and features

3. **Usage Tracking**:
   - Users input meter readings with dates
   - System calculates consumption between readings
   - Historical data is stored for trend analysis

4. **Cost Calculation**:
   - Users select applicable tariffs or input custom rates
   - System applies rates to usage data
   - Cost breakdown shows base charges, taxes, and total

5. **Visualization**:
   - Usage data is presented in interactive charts
   - Time-based trends show patterns by day, week, month, or year
   - Comparisons against previous periods highlight changes

### Technical Process Flow

1. **HTTP Request**: User action generates HTTP request to specific controller endpoint
2. **Controller Processing**: 
   - Controller receives request
   - Parameters and form data are validated
   - Authentication status is verified
3. **Service Layer**:
   - Business logic processes the request
   - Data operations are performed
   - Calculations and data transformations occur
4. **Repository Interaction**:
   - JPA repositories manage database operations
   - Entity persistence and retrieval
   - Transaction management ensures data integrity
5. **Response Generation**:
   - Controller prepares model attributes
   - Thymeleaf templates are populated with data
   - Response is rendered and returned to client

## Database Schema

The application uses a relational database with the following key tables:

### Users Table
- **id**: Primary key
- **email**: User's email address (unique)
- **password**: Hashed password
- **name**: User's full name
- **created_at**: Account creation timestamp
- **email_verified**: Boolean indicating email verification status

### Usage_Records Table
- **id**: Primary key
- **user_id**: Foreign key to Users table
- **reading_date**: Date of meter reading
- **meter_reading**: Electricity meter value
- **consumption**: Calculated consumption since previous reading
- **notes**: Optional user notes

### Tariffs Table
- **id**: Primary key
- **user_id**: Foreign key to Users table (null for default tariffs)
- **name**: Tariff name/description
- **rate_per_kwh**: Cost per kilowatt-hour
- **standing_charge**: Daily fixed charge
- **valid_from**: Start date of tariff validity
- **valid_to**: End date of tariff validity (null for current tariff)

### OTP_Verification Table
- **id**: Primary key
- **user_id**: Foreign key to Users table
- **otp**: One-time password hash
- **purpose**: Purpose of OTP (EMAIL_VERIFICATION, PASSWORD_RESET)
- **created_at**: Creation timestamp
- **expiry_date**: Expiration timestamp
- **used**: Boolean indicating if OTP has been used

## Security Features

The application implements several security measures:

### Authentication
- Password encryption using BCrypt
- Session-based authentication
- Remember-me functionality for persistent login

### Data Protection
- Password storage as secure hashes, not plaintext
- Input validation to prevent injection attacks
- CSRF protection for form submissions

### Access Control
- Role-based page access
- Secure endpoints requiring authentication
- Route protection for sensitive operations

## Installation and Setup

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Git

### Local Development Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/electric-usage-tracker.git
   cd electric-usage-tracker
   ```

2. **Configure the database**:
   - Create a PostgreSQL database named `electric_usage_tracker`
   - Update `src/main/resources/application.properties` with your database credentials

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - Open your browser and navigate to `http://localhost:8080`

### Database Setup

Create the database and user:

```sql
CREATE DATABASE electric_usage_tracker;
CREATE USER tracker_user WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE electric_usage_tracker TO tracker_user;
```

The application will automatically create necessary tables on first run due to the `spring.jpa.hibernate.ddl-auto=update` setting.

## Deployment Guide

### Deploying to Render

1. **Create a new Web Service**:
   - Connect your GitHub repository
   - Select the main branch
   - Choose "Java" as the Runtime

2. **Configure build settings**:
   - Build Command: `./mvnw clean package -DskipTests`
   - Start Command: `java -jar target/electric-usage-tracker-0.0.1-SNAPSHOT.jar`

3. **Add environment variables**:
   - `SPRING_DATASOURCE_URL`: Your PostgreSQL connection string
   - `SPRING_DATASOURCE_USERNAME`: Database username
   - `SPRING_DATASOURCE_PASSWORD`: Database password
   - `SPRING_PROFILES_ACTIVE`: Set to `prod`
   - `APP_URL`: Your application URL (e.g., `https://your-app-name.onrender.com`)

4. **Create a production properties file**:
   - Create `src/main/resources/application-prod.properties`
   - Configure it to use environment variables:

```properties
# Production Database Configuration
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=false

# Application URL for links
app.url=${APP_URL}

# Production settings
spring.devtools.restart.enabled=false
spring.devtools.livereload.enabled=false
spring.thymeleaf.cache=true
spring.web.resources.static-locations=classpath:/static/
spring.thymeleaf.prefix=classpath:/templates/
```

## Future Enhancements

Potential improvements for future versions:

1. **Advanced Analytics**:
   - Machine learning for usage prediction
   - Anomaly detection for unusual consumption patterns
   - Personalized energy-saving recommendations

2. **Integration Features**:
   - Smart meter API connectivity
   - Energy provider data import
   - Export functionality to PDF and spreadsheets

3. **Enhanced Visualization**:
   - Heat maps showing usage patterns
   - Comparative analysis with similar households
   - Carbon footprint tracking

4. **Mobile App**:
   - Native mobile applications
   - Push notifications for billing alerts
   - Offline functionality

## Presentation Guide

### For Teammates (Technical Handover)

#### Key Points to Explain
1. **System Architecture**:
   - Explain the MVC pattern used in the application
   - Describe how the Spring components interact
   - Emphasize the modular design for maintainability

2. **Data Flow**:
   - Walk through a complete request-response cycle
   - Show how user data is processed and stored
   - Explain the validation and security mechanisms

3. **Code Structure**:
   - Highlight key classes and their responsibilities
   - Explain naming conventions and organization
   - Point out extension points for future features

4. **Database Interaction**:
   - Show how entities map to database tables
   - Explain the repository pattern and JPA usage
   - Discuss transaction management and data integrity

5. **Front-end Implementation**:
   - Explain the Thymeleaf templating system
   - Show how controllers provide data to views
   - Highlight responsive design features

6. **Troubleshooting Guide**:
   - Common issues and their solutions
   - How to read and interpret logs
   - Performance optimization tips

### For Lecturer Presentation (Project Overview)

#### Presentation Structure
1. **Introduction (2-3 minutes)**:
   - Project purpose and problem statement
   - Target audience and use cases
   - Key features overview

2. **System Demonstration (5-7 minutes)**:
   - User registration and login
   - Adding and viewing usage data
   - Cost calculation and visualization
   - Responsive design across devices

3. **Technical Implementation (5-7 minutes)**:
   - Architecture and design patterns
   - Technology stack and justification
   - Security features and data protection
   - Database design and optimization

4. **Development Process (3-5 minutes)**:
   - Project planning and requirements gathering
   - Agile methodology and iteration cycles
   - Testing strategy and quality assurance
   - Challenges faced and solutions implemented

5. **Learning Outcomes (2-3 minutes)**:
   - Skills acquired during development
   - Application of course concepts
   - Innovation and problem-solving
   - Future enhancement possibilities

6. **Q&A Preparation**:
   - Have answers ready for technical questions
   - Prepare examples for specific implementation details
   - Be ready to explain design decisions
   - Know the limitations and how they might be addressed

#### Presentation Tips
- Practice the live demo thoroughly to avoid unexpected issues
- Prepare screenshots as backup in case of technical difficulties
- Highlight the unique features and design choices
- Connect implementation decisions to course concepts
- Be honest about challenges and how they were overcome
- Emphasize practical applications and real-world relevance

---

This application was developed as a school project for [Course Name] at [Institution Name] by [Team Members' Names].

© 2025 Electric Usage Tracker Team. All rights reserved.

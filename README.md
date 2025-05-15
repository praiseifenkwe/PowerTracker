# PowerTracker - Electricity Usage Tracking Application

## Project Overview
PowerTracker is a comprehensive web application designed to help users monitor, analyze, and optimize their electricity consumption. This application enables individuals to track the energy usage of various household appliances, visualize consumption patterns, and receive insights to reduce electricity costs and promote energy efficiency.

## Key Features

### User Authentication and Management
- Secure user registration and login system
- OAuth2 social login integration (Google, Facebook, GitHub)
- Email verification for account security
- Password reset functionality
- User profile management with customizable settings

### Electricity Usage Tracking
- Add and track consumption for common household appliances
- Customizable appliance wattage and usage duration
- Support for different time units (hours, days, weeks, months)
- Automatic calculation of kWh consumption and cost

### Dashboard and Analytics
- Interactive dashboard with key metrics visualization
- Energy consumption trends with historical data comparison
- Device distribution charts showing which appliances consume the most power
- Efficiency score to evaluate and improve energy usage patterns
- Percentage change indicators to track improvement over time

### Usage Calculator
- Real-time calculator for estimating energy consumption of appliances
- Pre-populated database of common household appliances with average wattage
- Custom appliance entry option for specialized equipment
- Adjustable usage duration with different time units

### History Management
- Detailed history of all tracked usage records
- Filtering and sorting capabilities
- Export functionality for data analysis
- Record deletion and management

### Settings and Preferences
- Customizable electricity cost rates
- Currency preference settings (default: NGN)
- User interface customization options

## Technical Architecture

### Backend Architecture
- **Framework**: Spring Boot 3.5.0
- **Java Version**: Java 17
- **Database**: PostgreSQL for production
- **Security**: Spring Security with OAuth2 integration
- **Email Integration**: SendInBlue/Brevo Java SDK for email services

### Frontend Technologies
- **Template Engine**: Thymeleaf
- **CSS Framework**: Tailwind CSS
- **JavaScript Libraries**:
  - Chart.js for data visualization
  - ApexCharts for enhanced charts
  - Font Awesome for iconography
  - Day.js for date manipulation

### Data Model
- **User**: Stores user information, authentication details, and preferences
- **ApplianceUsage**: Records individual usage entries with energy calculations
- **OtpVerification**: Manages one-time passwords for email verification
- **PasswordResetToken**: Handles password reset functionality

### API Design
- RESTful API endpoints for CRUD operations
- JSON-based data exchange for frontend/backend communication
- Secure endpoints with authentication requirements

## Installation and Setup

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL database
- SMTP server access for email functionality

### Database Configuration
1. Create a PostgreSQL database
2. Configure database connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/powertracker
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Email Configuration
1. Set up SendInBlue/Brevo account
2. Configure email properties in `application.properties`:
   ```properties
   spring.mail.host=smtp.your-email-provider.com
   spring.mail.port=587
   spring.mail.username=your_email
   spring.mail.password=your_password
   ```

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/electric-usage-tracker.git
   ```
2. Navigate to the project directory:
   ```bash
   cd electric-usage-tracker
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access the application at `http://localhost:8080`

### Docker Deployment
The application includes a Dockerfile for containerized deployment:
```bash
docker build -t powertracker .
docker run -p 8080:8080 powertracker
```

## User Flow

### Registration and Login
1. New users can register using the sign-up form
2. Email verification is required to activate the account
3. Users can also login via OAuth2 providers (Google, Facebook, GitHub)
4. Password reset functionality is available for account recovery

### Adding Energy Usage
1. Navigate to the Usage Calculator section
2. Select an appliance from the dropdown or add a custom appliance
3. Enter wattage (if using custom appliance)
4. Specify usage duration and time unit (hours, days, weeks, months)
5. Click "Calculate" to see the energy consumption and cost
6. Save the calculation to add it to your history

### Viewing Analytics
1. The Dashboard provides an overview of energy usage
2. Analytics section offers detailed insights with various charts:
   - Energy consumption trends over time
   - Device distribution showing which appliances use the most power
   - Efficiency score calculations
   - Comparative analysis with previous periods

### Managing Records
1. Usage History section displays all tracked records
2. Filter and sort records by date or appliance
3. Delete individual records or clear all data
4. Export data for external analysis

### Customizing Settings
1. Settings section allows personalization of the application
2. Adjust electricity cost rates for accurate calculations
3. Change preferred currency
4. Customize user profile information

## Security Measures
- Password encryption using BCrypt
- CSRF protection for form submissions
- Secure session management
- OAuth2 integration for delegated authentication
- Email verification to prevent account abuse

## Browser Compatibility
PowerTracker is optimized for modern browsers including:
- Google Chrome (recommended)
- Mozilla Firefox
- Microsoft Edge
- Safari

## Performance Optimization
- Lazy loading of JavaScript resources
- Optimized database queries
- Caching mechanisms for frequently accessed data
- Responsive design for all device sizes

## Future Enhancements
- Mobile application integration
- Smart home device connectivity
- AI-powered recommendations for energy savings
- Community features for comparing usage with similar households
- Integration with utility providers for actual billing data

## Troubleshooting
- **Login Issues**: Ensure email verification is completed
- **Calculation Errors**: Verify wattage and duration inputs are correct
- **Display Problems**: Clear browser cache and reload
- **Data Not Saving**: Check database connection

## Support
For technical support or feature requests, please contact:
- Email: support@powertracker.com
- GitHub Issues: https://github.com/yourusername/electric-usage-tracker/issues

## Contributors
- [Your Name] - Lead Developer
- [Team Member 1] - UI/UX Design
- [Team Member 2] - Backend Development
- [Team Member 3] - Testing and Documentation

## License
This project is licensed under the [License Name] - see the LICENSE file for details.

---

© 2025 PowerTracker. All Rights Reserved.
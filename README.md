# Municipal Library Management System

## Project Overview
A comprehensive enterprise-level library management system built with Spring Boot for the SWE6002 Enterprise Systems Development course. The system provides robust functionality for managing books, authors, users, and loans with role-based access control.

## Assessment Completion Status ✅

### Functional Requirements Implementation

#### 1. User Management ✅
- **Registration & Authentication**: Secure user registration and login with BCrypt password encoding
- **Role-Based Access Control (RBAC)**: Three roles implemented:
  - **ADMIN**: Full system access (user management, all CRUD operations, reports)
  - **LIBRARIAN**: Library operations (book/author management, loan processing)
  - **MEMBER**: Basic access (view books, manage own loans, rate books)

#### 2. Book Management ✅
- **CRUD Operations**: Complete Create, Read, Update, Delete functionality
- **Book Details**: Title, ISBN (Primary Key), publication date, genre, summary, cover image
- **Author Management**: Many-to-Many relationship with authors
- **Stock Management**: Track total copies and available copies

#### 3. Search & Filtering ✅
- **Search Functionality**: Search books by title, author, ISBN, or genre
- **Advanced Filtering**: Filter by publication date, availability, genre
- **Author Search**: Search authors by name or nationality

#### 4. Inventory Management ✅
- **Stock Tracking**: Real-time tracking of available copies
- **Check-In/Check-Out**: Complete loan management system
- **Loan Status**: ACTIVE, RETURNED, OVERDUE, LOST
- **Due Date Management**: Automatic overdue detection

#### 5. Reporting ✅
- **Analytics Dashboard**: 
  - Total books, users, active loans
  - Most borrowed books
  - Books by genre distribution
  - Overdue loans statistics
- **Export Reports**: PDF generation with Apache PDFBox
- **Role-based Report Access**: Different reports for different roles

#### 6. Notifications (Optional - Prepared) ⚡
- **Infrastructure Ready**: NotificationScheduler configured
- **Email Service**: Spring Mail configuration available
- **Scheduled Tasks**: @Scheduled annotation ready for automated notifications

## Technical Architecture

### Technology Stack
- **Backend**: Spring Boot 3.3.5
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with BCrypt
- **Template Engine**: Thymeleaf (Server-side rendering)
- **UI Framework**: SB Admin 2 (Bootstrap-based)
- **Build Tool**: Maven
- **Reporting**: Apache PDFBox, POI (Excel)
- **Java Version**: 17

### Design Patterns Implemented
1. **MVC Pattern**: Clear separation of concerns
2. **Repository Pattern**: Data access abstraction
3. **Service Layer Pattern**: Business logic encapsulation
4. **DTO Pattern**: Data transfer objects for API communication
5. **Builder Pattern**: Used in entity construction
6. **Singleton Pattern**: Spring beans management
7. **Strategy Pattern**: Different report generation strategies

### Security Features
- **HTTPS**: SSL/TLS encryption enabled
- **Spring Security**: Form-based authentication
- **CSRF Protection**: Enabled by default
- **Password Encryption**: BCrypt with strength 10
- **Session Management**: Secure session handling
- **Remember Me**: Optional persistent login
- **Method-level Security**: @PreAuthorize annotations

## Project Structure
```
municipal-library-system/
├── src/main/java/com/municipality/library/
│   ├── config/         # Configuration classes (Security, HTTPS, etc.)
│   ├── controller/     # MVC Controllers
│   ├── dto/           # Data Transfer Objects
│   ├── entity/        # JPA Entities
│   ├── exception/     # Custom exceptions
│   ├── filter/        # Security filters
│   ├── repository/    # Spring Data repositories
│   ├── service/       # Business logic services
│   └── util/          # Utility classes
├── src/main/resources/
│   ├── static/        # CSS, JS, images
│   ├── templates/     # Thymeleaf templates
│   ├── application.properties
│   ├── data.sql       # Initial data
│   └── schema.sql     # Database schema
├── docs/              # Documentation
├── backup/            # Backup files
└── pom.xml           # Maven configuration
```

## Database Schema
- **users**: User accounts with authentication details
- **roles**: System roles (ADMIN, LIBRARIAN, MEMBER)
- **user_roles**: Many-to-Many relationship
- **books**: Book catalog with inventory
- **authors**: Author information
- **book_authors**: Many-to-Many relationship
- **loans**: Book borrowing records
- **book_ratings**: User ratings and reviews

## Installation & Setup

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Git

### Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/municipal-library-system.git
   cd municipal-library-system
   ```

2. Create MySQL database:
   ```sql
   CREATE DATABASE library_db;
   ```

3. Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the application:
   - HTTP: http://localhost:8080 (redirects to HTTPS)
   - HTTPS: https://localhost:8443

### Default Users
| Username  | Password     | Role |
|-----------|--------------|------|
| admin     | admin123     | ADMIN |
| librarian | librarian123 | LIBRARIAN |
| member    | member123    | MEMBER |

## Key Features Demonstration

### For Administrators
- User management (create, update, delete users)
- Assign roles to users
- View all system reports
- System configuration and settings

### For Librarians
- Complete book CRUD operations
- Author management
- Process checkouts and returns
- View inventory reports
- Manage overdue books

### For Members
- Search and browse book catalog
- View book details and availability
- Check loan history
- Rate and review books
- Update profile information

## API Endpoints Overview
- `/login` - Authentication
- `/dashboard` - Role-based dashboard
- `/books/**` - Book management
- `/authors/**` - Author management
- `/loans/**` - Loan processing
- `/users/**` - User management
- `/reports/**` - Report generation
- `/api/**` - REST endpoints (future expansion)

## Testing
- Unit tests for services
- Integration tests for repositories
- Controller tests with MockMvc
- Security configuration tests

## Future Enhancements
1. Email notifications for due dates
2. Book reservation system
3. Fine calculation for overdue books
4. Mobile responsive improvements
5. REST API for mobile apps
6. Advanced analytics with charts
7. Book recommendation system
8. Multi-language support

## Contributors
- Student Name: [Alexandros Liakopoulos]
- Student ID: [2121384]
- Course: SWE6002 Enterprise Systems Development
- Institution: University of Bolton / NYC
- Academic Year: 2024/2025

## License
This project is developed for academic purposes as part of the SWE6002 course assessment.

## Acknowledgments
- Course Instructor: Spyridon Mavros
- University of Bolton
- New York College

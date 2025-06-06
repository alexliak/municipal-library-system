# Requirements Backlog - Municipal Library Management System

## Overview
This document contains the complete requirements backlog for the Municipal Library Management System, organized as user stories with priorities and acceptance criteria.

## Priority Levels
- **P0** - Critical (Must Have)
- **P1** - High Priority (Should Have)
- **P2** - Medium Priority (Could Have)
- **P3** - Low Priority (Won't Have This Time)

---

## Sprint 1: Foundation & Authentication (COMPLETED ✅)

### STORY-001: User Registration [P0]
**As a** visitor  
**I want to** register for a library account  
**So that** I can borrow books and access member features  

**Acceptance Criteria:**
- ✅ Registration form with username, password, email, full name
- ✅ Email validation and uniqueness check
- ✅ Password strength requirements (min 8 chars, mixed case, number, special char)
- ✅ BCrypt password encryption
- ✅ Default MEMBER role assignment
- ✅ Success message and redirect to login

**Implementation:** `UserController.registerUser()`, `UserService.createUser()`

### STORY-002: User Login [P0]
**As a** registered user  
**I want to** log into the system  
**So that** I can access features based on my role  

**Acceptance Criteria:**
- ✅ Form-based authentication with Spring Security
- ✅ Remember-me functionality
- ✅ Role-based dashboard redirection
- ✅ Session management
- ✅ Logout functionality
- ✅ HTTPS enforcement

**Implementation:** `SecurityConfig`, `UserDetailsServiceImpl`

### STORY-003: Role-Based Access Control [P0]
**As an** administrator  
**I want to** have different user roles  
**So that** access can be controlled appropriately  

**Acceptance Criteria:**
- ✅ Three roles: ADMIN, LIBRARIAN, MEMBER
- ✅ Role-based menu visibility
- ✅ Method-level security with @PreAuthorize
- ✅ Access denied page for unauthorized access
- ✅ Role hierarchy implementation

**Implementation:** `Role` entity, `SecurityConfig`

---

## Sprint 2: Book & Author Management (COMPLETED ✅)

### STORY-004: Book Catalog Management [P0]
**As a** librarian  
**I want to** add, edit, and delete books  
**So that** the catalog stays current  

**Acceptance Criteria:**
- ✅ CRUD operations for books
- ✅ ISBN as primary key
- ✅ Fields: title, publication date, genre, summary, cover image URL
- ✅ Inventory tracking (total copies, available copies)
- ✅ Form validation
- ✅ Success/error messages

**Implementation:** `BookController`, `BookService`, `Book` entity

### STORY-005: Author Management [P0]
**As a** librarian  
**I want to** manage author information  
**So that** books can be properly attributed  

**Acceptance Criteria:**
- ✅ CRUD operations for authors
- ✅ Fields: name, biography, birth date, nationality
- ✅ Many-to-many relationship with books
- ✅ Author search functionality
- ✅ Cascade operations handling

**Implementation:** `AuthorController`, `AuthorService`, `Author` entity

### STORY-006: Book-Author Relationships [P0]
**As a** librarian  
**I want to** associate multiple authors with books  
**So that** co-authored works are properly represented  

**Acceptance Criteria:**
- ✅ Many-to-many relationship implementation
- ✅ Add/remove authors from books
- ✅ Add/remove books from authors
- ✅ Bidirectional synchronization
- ✅ Junction table with proper constraints

**Implementation:** `book_authors` table, helper methods in entities

---

## Sprint 3: Search & Discovery (COMPLETED ✅)

### STORY-007: Basic Book Search [P0]
**As a** library user  
**I want to** search for books  
**So that** I can find what I want to read  

**Acceptance Criteria:**
- ✅ Search by title (partial match)
- ✅ Search by author name
- ✅ Search by ISBN
- ✅ Case-insensitive search
- ✅ Results display with availability status

**Implementation:** `BookRepository.searchBooksAndAuthors()`

### STORY-008: Advanced Filtering [P1]
**As a** library user  
**I want to** filter search results  
**So that** I can narrow down my options  

**Acceptance Criteria:**
- ✅ Filter by genre
- ✅ Filter by availability
- ✅ Filter by publication date range
- ✅ Combine multiple filters
- ✅ Clear filters option

**Implementation:** `BookController.searchBooks()` with criteria

### STORY-009: Author Search [P1]
**As a** library user  
**I want to** search for authors  
**So that** I can find all books by a specific author  

**Acceptance Criteria:**
- ✅ Search by author name
- ✅ Search by nationality
- ✅ View author details with book list
- ✅ Link from author to their books

**Implementation:** `AuthorController.searchAuthors()`

---

## Sprint 4: Loan Management (COMPLETED ✅)

### STORY-010: Book Checkout [P0]
**As a** librarian  
**I want to** check out books to members  
**So that** loans are properly tracked  

**Acceptance Criteria:**
- ✅ Select member and book for checkout
- ✅ Validate book availability
- ✅ Create loan record with due date (14 days)
- ✅ Update available copies count
- ✅ Prevent duplicate active loans
- ✅ Transaction integrity

**Implementation:** `LoanController.checkoutBook()`, `LoanService`

### STORY-011: Book Return [P0]
**As a** librarian  
**I want to** process book returns  
**So that** books become available again  

**Acceptance Criteria:**
- ✅ Mark loan as returned
- ✅ Set return date
- ✅ Update available copies count
- ✅ Calculate if return is late
- ✅ Update loan status

**Implementation:** `LoanController.returnBook()`

### STORY-012: View Member Loans [P0]
**As a** member  
**I want to** see my current and past loans  
**So that** I know what I have borrowed  

**Acceptance Criteria:**
- ✅ View active loans with due dates
- ✅ View loan history
- ✅ Sort by date
- ✅ See overdue status
- ✅ Access only own loans (security)

**Implementation:** `LoanController.viewMyLoans()`

### STORY-013: Overdue Management [P1]
**As a** librarian  
**I want to** see and manage overdue loans  
**So that** I can follow up with members  

**Acceptance Criteria:**
- ✅ Automated overdue status update
- ✅ Overdue loans dashboard
- ✅ Filter loans by status
- ✅ Due date highlighting
- ✅ Scheduled task for status updates

**Implementation:** `NotificationScheduler.checkOverdueLoans()`

---

## Sprint 5: Reporting & Analytics (COMPLETED ✅)

### STORY-014: Library Statistics Dashboard [P1]
**As an** administrator  
**I want to** see library statistics  
**So that** I can make informed decisions  

**Acceptance Criteria:**
- ✅ Total books, users, active loans
- ✅ Books by genre distribution
- ✅ Most borrowed books
- ✅ Overdue loans count
- ✅ Real-time updates

**Implementation:** `HomeController.showAdminDashboard()`

### STORY-015: Report Generation [P1]
**As an** administrator  
**I want to** generate reports  
**So that** I can share library metrics  

**Acceptance Criteria:**
- ✅ PDF export functionality
- ✅ Inventory report
- ✅ Loan statistics report
- ✅ User activity report
- ✅ Date range selection

**Implementation:** `ReportController`, `ReportService` with PDFBox

### STORY-016: Low Stock Alerts [P2]
**As a** librarian  
**I want to** see books with low availability  
**So that** I can order more copies  

**Acceptance Criteria:**
- ✅ Configure low stock threshold
- ✅ Dashboard widget for low stock
- ✅ List books below threshold
- ✅ Export low stock report

**Implementation:** `BookRepository.findByAvailableCopiesLessThanEqual()`

---

## Sprint 6: User Experience (COMPLETED ✅)

### STORY-017: Book Ratings [P2]
**As a** member  
**I want to** rate and review books  
**So that** others can benefit from my opinion  

**Acceptance Criteria:**
- ✅ 1-5 star rating system
- ✅ Optional text review
- ✅ One rating per user per book
- ✅ Average rating display
- ✅ Update existing ratings

**Implementation:** `BookRating` entity, `BookController`

### STORY-018: User Profile Management [P1]
**As a** user  
**I want to** update my profile  
**So that** my information stays current  

**Acceptance Criteria:**
- ✅ Update email, phone, address
- ✅ Change password with verification
- ✅ View account creation date
- ✅ See assigned roles
- ✅ Profile validation

**Implementation:** `UserController.updateProfile()`

### STORY-019: Responsive Design [P1]
**As a** user  
**I want to** access the system on any device  
**So that** I can use it anywhere  

**Acceptance Criteria:**
- ✅ Mobile-responsive layout
- ✅ Touch-friendly interfaces
- ✅ Readable on all screen sizes
- ✅ SB Admin 2 responsive theme

**Implementation:** Bootstrap responsive classes

---

## Future Enhancements (Sprint 7+) [P3]

### STORY-020: Email Notifications
**As a** member  
**I want to** receive email reminders  
**So that** I don't forget to return books  

**Status:** Infrastructure ready, not activated

### STORY-021: Book Reservations
**As a** member  
**I want to** reserve unavailable books  
**So that** I get them when returned  

**Status:** Not implemented

### STORY-022: Fine Calculation
**As a** librarian  
**I want to** automatically calculate fines  
**So that** late returns are penalized  

**Status:** Not implemented

### STORY-023: REST API
**As a** developer  
**I want to** access system via API  
**So that** mobile apps can be built  

**Status:** Not implemented

---

## Technical Debt & Improvements

### TECH-001: Unit Test Coverage [P1]
- Target: 80% coverage
- Current: Basic tests only
- Need: Service and controller tests

### TECH-002: API Documentation [P2]
- Add Swagger/OpenAPI
- Document all endpoints
- Generate client SDKs

### TECH-003: Performance Optimization [P2]
- Add caching layer
- Optimize database queries
- Implement pagination

### TECH-004: Security Hardening [P1]
- Add rate limiting
- Implement account lockout
- Add 2FA support

---

## Metrics

### Velocity Tracking
- Sprint 1: 3 stories (15 points)
- Sprint 2: 3 stories (18 points)
- Sprint 3: 3 stories (12 points)
- Sprint 4: 4 stories (20 points)
- Sprint 5: 3 stories (15 points)
- Sprint 6: 3 stories (10 points)

### Completion Rate
- Total Stories: 19
- Completed: 19
- Success Rate: 100%

### Technical Statistics
- Total Entities: 6
- Total Controllers: 7
- Total Services: 6
- Total Repositories: 6
- Lines of Code: ~5000

---

*Document maintained for SWE6002 Enterprise Systems Development*  
*Last Updated: June 6, 2025*
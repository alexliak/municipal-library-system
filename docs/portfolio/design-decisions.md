# Design Decisions Document - Municipal Library Management System

## Executive Summary
This document captures the key architectural and design decisions made during the development of the Municipal Library Management System. Each decision is documented with context, alternatives considered, and rationale for the chosen approach.

---

## 1. Architecture Pattern Decision

### Decision: Traditional MVC with Server-Side Rendering

**Context:**  
The system needed to be web-based and accessible to users with varying technical capabilities and devices.

**Alternatives Considered:**
1. **REST API + SPA (React/Angular)** - Separate frontend and backend
2. **Microservices Architecture** - Distributed services
3. **Traditional MVC with Thymeleaf** - Server-side rendering

**Decision Rationale:**
- **Simplicity**: Single deployment unit reduces operational complexity
- **SEO Friendly**: Server-rendered pages are immediately indexable
- **Security**: No exposed API endpoints reduce attack surface
- **Performance**: Faster initial page loads, especially on slower devices
- **Academic Fit**: Demonstrates enterprise patterns clearly

**Trade-offs:**
- Less interactive UI compared to SPA
- Tighter coupling between view and controller
- Full page reloads for navigation

**Implementation:**
```java
@Controller  // Not @RestController
public class BookController {
    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";  // Returns view name, not JSON
    }
}
```

---

## 2. Database Design Decisions

### Decision: Relational Database with JPA/Hibernate

**Context:**  
Library data has clear relationships (books-authors, users-loans) requiring ACID compliance.

**Alternatives Considered:**
1. **NoSQL (MongoDB)** - Document store
2. **Graph Database (Neo4j)** - For complex relationships
3. **Relational (MySQL)** - Traditional RDBMS

**Decision Rationale:**
- **Data Integrity**: Foreign keys ensure referential integrity
- **ACID Compliance**: Critical for loan transactions
- **Mature Ecosystem**: Excellent Spring Data JPA support
- **Query Flexibility**: Complex queries for reporting
- **Industry Standard**: Most enterprises use RDBMS

**Key Design Choices:**

1. **ISBN as Book Primary Key**
   ```java
   @Entity
   @Table(name = "books")
   public class Book {
       @Id
       @Column(length = 13)
       private String isbn;  // Natural key instead of surrogate
   }
   ```
   - Natural business identifier
   - Prevents duplicate books
   - Industry standard

2. **Many-to-Many with Helper Methods**
   ```java
   public void addAuthor(Author author) {
       this.authors.add(author);
       author.getBooks().add(this);  // Bidirectional sync
   }
   ```
   - Maintains referential integrity
   - Prevents orphaned relationships

3. **Audit Fields Pattern**
   ```java
   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;
   ```
   - Track entity lifecycle
   - Support compliance requirements

---

## 3. Security Architecture

### Decision: Spring Security with Form-Based Authentication

**Context:**  
System handles sensitive user data and must prevent unauthorized access.

**Alternatives Considered:**
1. **OAuth2/OIDC** - Third-party authentication
2. **JWT Tokens** - Stateless authentication
3. **Form-Based + Session** - Traditional approach

**Decision Rationale:**
- **Simplicity**: No external dependencies
- **Security**: Sessions harder to steal than JWT
- **User Experience**: Remember-me functionality
- **CSRF Protection**: Built-in with Spring Security

**Implementation Decisions:**

1. **BCrypt Password Encoding**
   ```java
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder(10);  // Cost factor 10
   }
   ```
   - Industry standard
   - Adaptive cost factor
   - Salt included

2. **HTTPS Enforcement**
   ```java
   .requiresChannel(channel -> channel
       .anyRequest().requiresSecure()
   )
   ```
   - All traffic encrypted
   - Prevents session hijacking

3. **Role Hierarchy**
   - ADMIN → inherits all permissions
   - LIBRARIAN → library operations
   - MEMBER → basic access
   - Simplifies permission management

---

## 4. Technology Stack Decisions

### Decision: Spring Boot 3.x with Embedded Tomcat

**Context:**  
Need for rapid development with production-ready features.

**Rationale:**
- **Convention over Configuration**: Minimal boilerplate
- **Embedded Server**: Simplified deployment
- **Starter Dependencies**: Curated, compatible versions
- **Production Features**: Health checks, metrics built-in
- **Community**: Extensive documentation and support

### Decision: Thymeleaf Template Engine

**Context:**  
Need for dynamic HTML generation with Spring integration.

**Alternatives Considered:**
1. **JSP** - Traditional Java templating
2. **Freemarker** - Powerful but complex
3. **Thymeleaf** - Natural templating

**Rationale:**
- **Natural Templates**: Valid HTML, designer-friendly
- **Spring Integration**: Excellent Spring Security support
- **Expression Language**: Powerful and intuitive
- **Fragment Reuse**: DRY principle support

**Example:**
```html
<div th:each="book : ${books}" 
     th:if="${book.availableCopies > 0}">
    <h3 th:text="${book.title}">Book Title</h3>
    <span sec:authorize="hasRole('LIBRARIAN')">
        <a th:href="@{/books/edit/{id}(id=${book.isbn})}">Edit</a>
    </span>
</div>
```

---

## 5. Package Structure Decision

### Decision: Layer-Based Package Organization

**Structure:**
```
com.municipality.library/
├── config/       # Configuration classes
├── controller/   # Web layer
├── service/      # Business logic
├── repository/   # Data access
├── entity/       # Domain models
├── dto/          # Data transfer objects
└── exception/    # Custom exceptions
```

**Rationale:**
- **Clear Separation**: Each layer has distinct responsibility
- **Easy Navigation**: Developers know where to find code
- **Spring Convention**: Follows Spring best practices
- **Testability**: Easy to mock layers

**Alternative Rejected:**
- Feature-based packaging (e.g., `book/`, `user/`)
- Would scatter related technical concerns

---

## 6. Error Handling Strategy

### Decision: Global Exception Handler with Custom Error Pages

**Implementation:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookNotFoundException.class)
    public String handleBookNotFound(Model model) {
        model.addAttribute("error", "Book not found");
        return "error/404";
    }
}
```

**Rationale:**
- **Consistent UX**: All errors handled uniformly
- **Security**: No stack traces exposed
- **Maintainability**: Centralized error handling
- **User-Friendly**: Custom error pages

---

## 7. Data Validation Strategy

### Decision: Multi-Layer Validation

**Layers:**
1. **Frontend**: HTML5 validation attributes
2. **Controller**: `@Valid` annotation
3. **Service**: Business rule validation
4. **Database**: Constraints

**Example:**
```java
@PostMapping("/books")
public String addBook(@Valid @ModelAttribute Book book, 
                     BindingResult result) {
    if (result.hasErrors()) {
        return "books/form";
    }
    // Additional business validation in service
    bookService.save(book);
    return "redirect:/books";
}
```

**Rationale:**
- **Defense in Depth**: Multiple validation points
- **User Experience**: Immediate feedback
- **Data Integrity**: Database constraints as last resort

---

## 8. State Management Decision

### Decision: Server-Side Sessions

**Context:**  
Need to maintain user state across requests.

**Alternatives Considered:**
1. **Client-Side State** - Local storage
2. **Stateless JWT** - Token-based
3. **Server Sessions** - Traditional approach

**Rationale:**
- **Security**: State not exposed to client
- **Simplicity**: Spring Session handling
- **Scalability**: Can use Redis for distributed sessions
- **Features**: Built-in timeout, concurrent session control

---

## 9. Search Implementation

### Decision: JPA Specifications with Dynamic Queries

**Implementation:**
```java
@Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
       "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
       "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
List<Book> searchBooksAndAuthors(@Param("query") String query);
```

**Rationale:**
- **Type Safety**: Compile-time query validation
- **Performance**: Database-level filtering
- **Flexibility**: Easy to add criteria
- **SQL Injection Safe**: Parameterized queries

---

## 10. UI/UX Framework Decision

### Decision: SB Admin 2 Bootstrap Theme

**Context:**  
Need professional, responsive UI without custom design.

**Rationale:**
- **Professional Look**: Enterprise-appropriate
- **Responsive**: Mobile-friendly out of box
- **Component Library**: Pre-built widgets
- **Customizable**: CSS variables for theming
- **Documentation**: Well-documented components

**Implementation Benefits:**
- Consistent design language
- Reduced development time
- Accessibility features included
- Cross-browser compatibility

---

## 11. Testing Strategy

### Decision: Layered Testing Approach

**Levels:**
1. **Unit Tests**: Services with mocked dependencies
2. **Integration Tests**: Repository layer with H2
3. **Web Tests**: Controllers with MockMvc
4. **End-to-End**: Manual testing (time constraints)

**Rationale:**
- **Fast Feedback**: Unit tests run quickly
- **Confidence**: Integration tests verify queries
- **Isolation**: Each layer tested independently

---

## 12. Deployment Strategy

### Decision: Single JAR with Embedded Server

**Build Configuration:**
```xml
<packaging>jar</packaging>
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

**Rationale:**
- **Simplicity**: Single artifact to deploy
- **Portability**: Runs anywhere with Java
- **Version Control**: Server version bundled
- **Cloud Ready**: Easy container deployment

---

## Performance Considerations

### Decisions Made:
1. **Lazy Loading**: `@ManyToMany(fetch = FetchType.LAZY)`
2. **Database Indexes**: On frequently queried columns
3. **Connection Pooling**: HikariCP default
4. **Query Optimization**: Avoid N+1 problems

### Future Optimizations:
1. Add caching layer (Redis)
2. Implement pagination
3. Database query optimization
4. Static resource CDN

---

## Lessons Learned

### What Worked Well:
1. **Spring Boot**: Rapid development and configuration
2. **Thymeleaf**: Natural templating improved developer experience
3. **JPA**: Reduced boilerplate code significantly
4. **Bootstrap Theme**: Professional UI without designer

### Challenges Faced:
1. **Bidirectional Relationships**: Required careful handling
2. **Transaction Boundaries**: Needed explicit demarcation
3. **Security Configuration**: Initial learning curve
4. **Date Handling**: Timezone considerations

### Recommendations:
1. Start with security configuration early
2. Design database schema before entities
3. Use DTOs for complex views
4. Implement logging strategy from start

---

## Conclusion

The architectural decisions made for the Municipal Library Management System prioritized:
- **Simplicity** over complexity
- **Security** over features
- **Maintainability** over performance
- **Convention** over configuration

These decisions resulted in a secure, and maintainable system that meets all functional requirements while remaining extensible for future enhancements.

---

*Document Version: 1.0*  
*Last Updated: June 6, 2025*  
*Author: SWE6002 Student*
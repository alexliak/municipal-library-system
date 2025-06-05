package com.municipality.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class PasswordGeneratorTest {
    
    @Test
    public void generatePasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("\n=== Municipal Library System - Password Generation ===");
        System.out.println("Generated using Spring Boot Test Framework");
        System.out.println("Date: " + java.time.LocalDateTime.now());
        System.out.println();
        
        // Generate for admin/admin123
        String adminHash = encoder.encode("admin123");
        System.out.println("-- For admin/admin123:");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println();
        
        // Generate for librarian/librarian123
        String librarianHash = encoder.encode("librarian123");
        System.out.println("-- For librarian/librarian123:");
        System.out.println("UPDATE users SET password = '" + librarianHash + "' WHERE username = 'librarian';");
        System.out.println();
        
        // Generate for member/member123
        String memberHash = encoder.encode("member123");
        System.out.println("-- For member/member123:");
        System.out.println("UPDATE users SET password = '" + memberHash + "' WHERE username = 'member';");
        System.out.println();
        
        // Verification
        System.out.println("=== Hash Verification ===");
        System.out.println("✓ admin123 matches generated hash: " + encoder.matches("admin123", adminHash));
        System.out.println("✓ librarian123 matches generated hash: " + encoder.matches("librarian123", librarianHash));
        System.out.println("✓ member123 matches generated hash: " + encoder.matches("member123", memberHash));
        System.out.println();
        System.out.println("=== Copy the SQL statements above and run in MySQL ===");
    }
}

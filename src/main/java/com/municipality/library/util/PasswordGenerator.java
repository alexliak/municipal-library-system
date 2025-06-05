package com.municipality.library.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== Municipal Library Password Hashes ===");
        System.out.println();
        
        // Generate for admin/admin123
        String adminHash = encoder.encode("admin123");
        System.out.println("admin/admin123:");
        System.out.println("UPDATE users SET password = '" + adminHash + "' WHERE username = 'admin';");
        System.out.println();
        
        // Generate for librarian/librarian123
        String librarianHash = encoder.encode("librarian123");
        System.out.println("librarian/librarian123:");
        System.out.println("UPDATE users SET password = '" + librarianHash + "' WHERE username = 'librarian';");
        System.out.println();
        
        // Generate for member/member123
        String memberHash = encoder.encode("member123");
        System.out.println("member/member123:");
        System.out.println("UPDATE users SET password = '" + memberHash + "' WHERE username = 'member';");
        System.out.println();
        
        // Test that they work
        System.out.println("=== Verification ===");
        System.out.println("admin123 matches adminHash: " + encoder.matches("admin123", adminHash));
        System.out.println("librarian123 matches librarianHash: " + encoder.matches("librarian123", librarianHash));
        System.out.println("member123 matches memberHash: " + encoder.matches("member123", memberHash));
    }
}

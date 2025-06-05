package com.municipality.library;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class QuickPasswordTest {
    
    @Test
    public void testLibrarianPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Test if current hash matches librarian123
        String currentHash = "$2a$10$sF3HKjrVb/Q.rg2h7LCMOOcVrtA14Bofbd18m3cnl62Iy45tPbLo6";
        boolean matches = encoder.matches("librarian123", currentHash);
        
        System.out.println("Current hash matches 'librarian123': " + matches);
        
        if (!matches) {
            // Generate new hash
            String newHash = encoder.encode("librarian123");
            System.out.println("\nNEW HASH for librarian123:");
            System.out.println(newHash);
            System.out.println("\nSQL to update:");
            System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'librarian';");
        }
    }
}

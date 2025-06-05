package com.municipality.library;

import org.junit.jupiter.api.Test;
import java.security.MessageDigest;

public class SHA256Test {
    
    @Test
    public void generateAllHashes() throws Exception {
        String[] passwords = {"admin123", "librarian123", "member123"};
        
        System.out.println("=== SHA-256 Hashes for PasswordHashFilter ===\n");
        
        for (String password : passwords) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            System.out.println("Password: " + password);
            System.out.println("SHA-256:  " + hexString.toString());
            System.out.println("Add to filter: knownHashes.put(\"" + hexString.toString() + "\", \"" + password + "\");");
            System.out.println();
        }
    }
}

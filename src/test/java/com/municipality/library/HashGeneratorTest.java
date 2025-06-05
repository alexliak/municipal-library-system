package com.municipality.library;

import org.junit.jupiter.api.Test;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGeneratorTest {
    
    @Test
    public void generateSHA256Hashes() throws NoSuchAlgorithmException {
        System.out.println("\n=== SHA-256 Hash Generator ===\n");
        
        // Generate hashes for our passwords
        String[] passwords = {"admin123", "librarian123", "member123"};
        
        for (String password : passwords) {
            String hash = sha256(password);
            System.out.println("Password: " + password);
            System.out.println("SHA-256:  " + hash);
            System.out.println();
        }
    }
    
    private String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}

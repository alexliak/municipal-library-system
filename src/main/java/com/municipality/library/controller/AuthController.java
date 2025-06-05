package com.municipality.library.controller;

import com.municipality.library.entity.User;
import com.municipality.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@RequestParam String fullName,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              ModelMap model) {
        
        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }
        
        // Check if username exists
        if (userService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }
        
        // Check if email exists
        if (userService.existsByEmail(email)) {
            model.addAttribute("error", "Email already registered!");
            return "register";
        }
        
        try {
            // Create new user
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // UserService will encode it
            
            // Register user (will be assigned MEMBER role by default)
            userService.register(newUser);
            
            model.addAttribute("success", true);
            return "register";
            
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}

package com.municipality.library.controller;

import com.municipality.library.entity.User;
import com.municipality.library.entity.Role;
import com.municipality.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // List all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(ModelMap model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        return "users/list";
    }

    // Show user profile (for logged in user)
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, ModelMap model) {
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (!userOpt.isPresent()) {
            return "redirect:/";
        }
        
        model.addAttribute("user", userOpt.get());
        return "users/profile";
    }

    // Update profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User updatedUser,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        Optional<User> currentUserOpt = userService.findByUsername(userDetails.getUsername());
        if (!currentUserOpt.isPresent()) {
            return "redirect:/";
        }
        
        User currentUser = currentUserOpt.get();
        
        // Update only allowed fields
        currentUser.setFullName(updatedUser.getFullName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setAddress(updatedUser.getAddress());
        
        try {
            userService.updateUser(currentUser.getUserId(), currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users/profile";
    }

    // View specific user (Admin only)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewUser(@PathVariable Integer userId, ModelMap model) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            return "redirect:/users?error=notfound";
        }
        
        model.addAttribute("user", userOpt.get());
        return "users/detail";
    }

    // Show register form (Admin only)
    @GetMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRegisterForm(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userService.findAllRoles());
        return "users/register";
    }

    // Process register form (Admin only)
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String registerUser(@Valid @ModelAttribute User user,
                              BindingResult result,
                              @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                              RedirectAttributes redirectAttributes,
                              ModelMap model) {
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/register";
        }
        
        // Check if username already exists
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists!");
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/register";
        }
        
        try {
            // Register the user with selected roles
            User newUser = userService.register(user);
            
            // Add selected roles
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    userService.addRoleToUser(newUser.getUserId(), roleId);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User '" + user.getUsername() + "' registered successfully!");
            return "redirect:/users";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error registering user: " + e.getMessage());
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/register";
        }
    }

    // Show add user form (Admin only)
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", userService.findAllRoles());
        return "users/form";
    }

    // Process add user form (Admin only)
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(@Valid @ModelAttribute User user,
                          BindingResult result,
                          @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                          RedirectAttributes redirectAttributes,
                          ModelMap model) {
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/form";
        }
        
        // Check if username already exists
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "Username already exists!");
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/form";
        }
        
        try {
            // Register the user with selected roles
            User newUser = userService.register(user);
            
            // Add selected roles
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Integer roleId : roleIds) {
                    userService.addRoleToUser(newUser.getUserId(), roleId);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User '" + user.getUsername() + "' added successfully!");
            return "redirect:/users";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding user: " + e.getMessage());
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/form";
        }
    }

    // Show edit user form (Admin only)
    @GetMapping("/edit/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditUserForm(@PathVariable Integer userId, ModelMap model) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            return "redirect:/users?error=notfound";
        }
        
        User user = userOpt.get();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.findAllRoles());
        model.addAttribute("selectedRoleIds", 
            user.getRoles().stream()
                .map(role -> role.getRoleId())
                .toList()
        );
        return "users/form";
    }

    // Process edit user form (Admin only)
    @PostMapping("/edit/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Integer userId,
                            @Valid @ModelAttribute User user,
                            BindingResult result,
                            @RequestParam(value = "roleIds", required = false) List<Integer> roleIds,
                            RedirectAttributes redirectAttributes,
                            ModelMap model) {
        
        if (result.hasErrors()) {
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/form";
        }
        
        try {
            // Update user
            user.setUserId(userId);
            userService.updateUser(userId, user);
            
            // Update roles
            userService.updateUserRoles(userId, roleIds);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "User '" + user.getUsername() + "' updated successfully!");
            return "redirect:/users";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
            model.addAttribute("allRoles", userService.findAllRoles());
            return "users/form";
        }
    }

    // Enable/Disable user (Admin only)
    @PostMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleUserStatus(@PathVariable Integer userId,
                                   RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.findById(userId);
        if (!userOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/users";
        }
        
        User user = userOpt.get();
        
        try {
            if (user.isEnabled()) {
                userService.disableUser(userId);
                redirectAttributes.addFlashAttribute("successMessage", "User disabled successfully");
            } else {
                userService.enableUser(userId);
                redirectAttributes.addFlashAttribute("successMessage", "User enabled successfully");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users";
    }

    // Delete user (Admin only)
    @PostMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Integer userId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
        
        // Don't allow deleting your own account
        Optional<User> currentUserOpt = userService.findByUsername(userDetails.getUsername());
        if (currentUserOpt.isPresent() && currentUserOpt.get().getUserId().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot delete your own account!");
            return "redirect:/users";
        }
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                userService.deleteUser(userId);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "User '" + userOpt.get().getUsername() + "' deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting user: " + e.getMessage());
        }
        
        return "redirect:/users";
    }

    // Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match");
            return "redirect:/users/profile";
        }
        
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (!userOpt.isPresent()) {
            return "redirect:/";
        }
        
        try {
            userService.changePassword(userOpt.get().getUserId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users/profile";
    }
    
    // Search users (Admin only)
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String searchUsers(@RequestParam("query") String query, ModelMap model) {
        List<User> users = userService.searchUsers(query);
        model.addAttribute("users", users);
        model.addAttribute("searchQuery", query);
        model.addAttribute("totalUsers", users.size());
        return "users/list";
    }
}

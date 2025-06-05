package com.municipality.library.service;

import com.municipality.library.entity.User;
import com.municipality.library.entity.Role;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    
    // Authentication
    User login(String username, String password);
    User register(User user);
    
    // CRUD operations
    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(User user);
    void deleteById(Integer id);
    
    // Business logic
    User updateUser(Integer userId, User updatedUser);
    void changePassword(Integer userId, String oldPassword, String newPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(String roleName);
    List<User> findUsersByRole(String roleName);
    void enableUser(Integer userId);
    void disableUser(Integer userId);
    void deleteUser(Integer userId);
    
    // Role management
    List<Role> findAllRoles();
    void addRoleToUser(Integer userId, Integer roleId);
    void removeRoleFromUser(Integer userId, Integer roleId);
    void updateUserRoles(Integer userId, List<Integer> roleIds);
    
    // Search
    List<User> searchUsers(String query);
    
    // Statistics
    long countUsers();
    long countActiveUsers();
    long countUsersByRole(String roleName);
}
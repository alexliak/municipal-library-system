package com.municipality.library.service.impl;

import com.municipality.library.entity.Role;
import com.municipality.library.entity.User;
import com.municipality.library.repository.RoleRepository;
import com.municipality.library.repository.UserRepository;
import com.municipality.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", username);
        
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
                
        log.debug("User found: {}, enabled: {}, roles: {}", 
                user.getUsername(), user.isEnabled(), user.getRoles());
        log.debug("Authorities: {}", user.getAuthorities());
        
        return user;
    }

    @Override
    public User login(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt.get();
        }
        return null;
    }

    @Override
    public User register(User user) {
        // Check if username exists
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default role if none assigned
        if (user.getRoles().isEmpty()) {
            Role memberRole = roleRepository.findByName("ROLE_MEMBER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            user.getRoles().add(memberRole);
        }
        
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(Integer userId, User updatedUser) {
        User existingUser = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFullName(updatedUser.getFullName());
        
        // Update password only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByRole(String roleName) {
        return userRepository.findByRolesName(roleName);
    }
    
    @Override
    public List<User> findUsersByRole(String roleName) {
        // Add ROLE_ prefix if not present
        String fullRoleName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return userRepository.findByRolesName(fullRoleName);
    }

    @Override
    public void enableUser(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disableUser(Integer userId) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countByEnabled(true);
    }

    @Override
    public long countUsersByRole(String roleName) {
        return userRepository.countByRolesName(roleName);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void addRoleToUser(Integer userId, Integer roleId) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Override
    public void updateUserRoles(Integer userId, List<Integer> roleIds) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Clear existing roles
        user.getRoles().clear();
        
        // Add new roles
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Integer roleId : roleIds) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
                user.getRoles().add(role);
            }
        }
        
        userRepository.save(user);
    }

    @Override
    public List<User> searchUsers(String query) {
        // Search by username, email, or full name
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                query, query, query);
    }
}
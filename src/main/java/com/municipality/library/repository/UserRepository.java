package com.municipality.library.repository;

import com.municipality.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // Find user by username with roles
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find user by username and password (for simple login - later will use Spring Security)
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    // Find all enabled users
    List<User> findByEnabledTrue();
    
    // Find users by role
    List<User> findByRolesName(String roleName);
    
    // Count enabled users
    long countByEnabled(boolean enabled);
    
    // Count users by role
    long countByRolesName(String roleName);
    
    // Count enabled users
    Long countByEnabledTrue();
    
    Long countByEnabledFalse();
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Long countByRole(@Param("roleName") String roleName);
    
    // Search users by username, email, or full name
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String username, String email, String fullName);
    
    // Additional queries for reporting
    Long countByRoles_Name(String roleName);
    
    Long countByCreatedAtAfterAndRoles_Name(LocalDateTime date, String roleName);
    
    Integer countByCreatedAtBetweenAndRoles_Name(LocalDateTime start, LocalDateTime end, String roleName);
    
    // New methods needed for ReportService
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :date")
    Long countUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    Integer countUsersCreatedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE u.enabled = :enabled AND r.name = :roleName")
    Long countByEnabledAndRolesName(@Param("enabled") boolean enabled, @Param("roleName") String roleName);
}

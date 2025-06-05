package com.municipality.library.repository;

import com.municipality.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    // Find author by name
    Optional<Author> findByName(String name);
    
    // Find authors by name containing
    List<Author> findByNameContainingIgnoreCase(String name);
    
    // Find authors by nationality
    List<Author> findByNationality(String nationality);
    
    // Check if author exists by name
    boolean existsByName(String name);
}

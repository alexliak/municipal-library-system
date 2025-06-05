package com.municipality.library.service;

import com.municipality.library.entity.Author;
import com.municipality.library.entity.Book;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
    
    // CRUD operations
    Author save(Author author);
    Optional<Author> findById(Long id);
    List<Author> findAll();
    void delete(Author author);
    void deleteById(Long id);
    
    // Search operations
    List<Author> searchByName(String name);
    List<Author> findByNationality(String nationality);
    
    // Book management
    Author addBook(Long authorId, Book book);
    Author removeBook(Long authorId, Book book);
    List<Book> getBooksByAuthor(Long authorId);
    
    // Statistics
    long countAuthors();
    List<Author> findMostProlificAuthors(int limit);
    List<Author> findByBookCount(int minBooks);
}

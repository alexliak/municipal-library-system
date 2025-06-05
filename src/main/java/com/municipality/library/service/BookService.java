package com.municipality.library.service;

import com.municipality.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BookService {
    
    // Basic CRUD operations
    Book saveBook(Book book);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAllBooks();
    Page<Book> findBooksPageable(Pageable pageable);
    void deleteBook(String isbn);
    Book updateBook(String isbn, Book book);
    
    // Search and filtering
    List<Book> searchBooks(String query);
    List<Book> findByGenre(String genre);
    List<Book> findByAuthor(Long authorId);
    List<Book> findAvailableBooks();
    
    // Inventory management
    void updateBookAvailability(String isbn, int change);
    int getAvailableCopies(String isbn);
    List<Book> findLowStockBooks(int threshold);
    
    // Statistics
    List<Book> findMostBorrowedBooks(int limit);
    long getTotalBookCount();
    long getAvailableBookCount();
    
    // Additional methods needed by controllers
    Optional<Book> findById(String isbn);
    boolean isAvailable(String isbn);
    void incrementAvailableCopies(String isbn);
    void decrementAvailableCopies(String isbn);
    long countBooks();
    long countAvailableBooks();
    List<Book> findRecentlyAddedBooks(int limit);
    List<Book> findTopRatedBooks(int limit);
}

package com.municipality.library.service.impl;

import com.municipality.library.entity.Author;
import com.municipality.library.entity.Book;
import com.municipality.library.exception.BookNotFoundException;
import com.municipality.library.exception.InsufficientStockException;
import com.municipality.library.repository.AuthorRepository;
import com.municipality.library.repository.BookRepository;
import com.municipality.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    
    @Override
    public Book saveBook(Book book) {
        
        // Ensure bidirectional relationship sync
        if (book.getAuthors() != null) {
            Set<Author> managedAuthors = book.getAuthors().stream()
                .map(author -> {
                    if (author.getAuthorId() != null) {
                        return authorRepository.findById(author.getAuthorId())
                            .orElse(author);
                    }
                    return author;
                })
                .collect(Collectors.toSet());
            
            book.setAuthors(managedAuthors);
            
            // Sync bidirectional relationship
            for (Author author : managedAuthors) {
                author.getBooks().add(book);
            }
        }
        
        // Set available copies to total copies if not specified
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        
        return bookRepository.save(book);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Book> findBooksPageable(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    @Override
    public void deleteBook(String isbn) {
        Book book = bookRepository.findById(isbn)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        
        // Remove book from all authors (bidirectional sync)
        for (Author author : book.getAuthors()) {
            author.getBooks().remove(book);
        }
        
        bookRepository.delete(book);
    }
    
    @Override
    public Book updateBook(String isbn, Book updatedBook) {
        
        Book existingBook = bookRepository.findById(isbn)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        
        // Update fields
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setPublicationDate(updatedBook.getPublicationDate());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setSummary(updatedBook.getSummary());
        existingBook.setCoverImageUrl(updatedBook.getCoverImageUrl());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());
        
        // Update available copies if total increased
        if (updatedBook.getTotalCopies() > existingBook.getTotalCopies()) {
            int difference = updatedBook.getTotalCopies() - existingBook.getTotalCopies();
            existingBook.setAvailableCopies(existingBook.getAvailableCopies() + difference);
        }
        
        // Update authors if provided
        if (updatedBook.getAuthors() != null) {
            // Remove book from old authors
            for (Author author : existingBook.getAuthors()) {
                author.getBooks().remove(existingBook);
            }
            
            // Clear and add new authors
            existingBook.getAuthors().clear();
            
            Set<Author> managedAuthors = updatedBook.getAuthors().stream()
                .map(author -> authorRepository.findById(author.getAuthorId())
                    .orElse(author))
                .collect(Collectors.toSet());
            
            existingBook.setAuthors(managedAuthors);
            
            // Add book to new authors
            for (Author author : managedAuthors) {
                author.getBooks().add(existingBook);
            }
        }
        
        return bookRepository.save(existingBook);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String query) {
        return bookRepository.searchBooksAndAuthors(query);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthor(Long authorId) {
        return bookRepository.findByAuthors_AuthorId(authorId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }
    
    @Override
    public void updateBookAvailability(String isbn, int change) {
        
        Book book = bookRepository.findById(isbn)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        
        int newAvailability = book.getAvailableCopies() + change;
        
        if (newAvailability < 0) {
            throw new InsufficientStockException(
                "Insufficient stock. Available: " + book.getAvailableCopies()
            );
        }
        
        if (newAvailability > book.getTotalCopies()) {
            throw new IllegalArgumentException(
                "Available copies cannot exceed total copies"
            );
        }
        
        book.setAvailableCopies(newAvailability);
        bookRepository.save(book);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getAvailableCopies(String isbn) {
        Book book = bookRepository.findById(isbn)
            .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return book.getAvailableCopies();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findLowStockBooks(int threshold) {
        return bookRepository.findByAvailableCopiesLessThanEqual(threshold);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findMostBorrowedBooks(int limit) {
        return bookRepository.findMostBorrowedBooks(limit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalBookCount() {
        return bookRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getAvailableBookCount() {
        return bookRepository.countByAvailableCopiesGreaterThan(0);
    }
    
    @Override
    public void incrementAvailableCopies(String isbn) {
        updateBookAvailability(isbn, 1);
    }
    
    @Override
    public void decrementAvailableCopies(String isbn) {
        updateBookAvailability(isbn, -1);
    }
    
    @Override
    public boolean isAvailable(String isbn) {
        return getAvailableCopies(isbn) > 0;
    }
    
    @Override
    public Optional<Book> findById(String isbn) {
        return findByIsbn(isbn);
    }
    
    @Override
    public long countBooks() {
        return getTotalBookCount();
    }
    
    @Override
    public long countAvailableBooks() {
        return getAvailableBookCount();
    }
    
    @Override
    public List<Book> findRecentlyAddedBooks(int limit) {
        // Simple implementation - returns the most recent books by limit
        return bookRepository.findAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Book> findTopRatedBooks(int limit) {
        // For now, return popular books or recently added
        // In a real implementation, this would query based on ratings
        return bookRepository.findMostBorrowedBooks(limit);
    }
}

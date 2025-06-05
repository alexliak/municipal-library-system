package com.municipality.library.service.impl;

import com.municipality.library.entity.Author;
import com.municipality.library.entity.Book;
import com.municipality.library.repository.AuthorRepository;
import com.municipality.library.repository.BookRepository;
import com.municipality.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }
    
    @Override
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }
    
    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
    
    @Override
    public void delete(Author author) {
        // Remove bidirectional relationships
        for (Book book : new ArrayList<>(author.getBooks())) {
            book.removeAuthor(author);
            bookRepository.save(book);
        }
        authorRepository.delete(author);
    }
    
    @Override
    public void deleteById(Long id) {
        Optional<Author> authorOpt = findById(id);
        if (authorOpt.isPresent()) {
            delete(authorOpt.get());
        }
    }
    
    @Override
    public List<Author> searchByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public List<Author> findByNationality(String nationality) {
        return authorRepository.findByNationality(nationality);
    }
    
    @Override
    public Author addBook(Long authorId, Book book) {
        Author author = findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
        
        Book finalBook = book;
        if (book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            finalBook = bookRepository.findById(book.getIsbn())
                    .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + book.getIsbn()));
        }
        
        author.addBook(finalBook);
        return authorRepository.save(author);
    }
    
    @Override
    public Author removeBook(Long authorId, Book book) {
        Author author = findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
        
        author.removeBook(book);
        return authorRepository.save(author);
    }
    
    @Override
    public List<Book> getBooksByAuthor(Long authorId) {
        Author author = findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
        return new ArrayList<>(author.getBooks());
    }
    
    @Override
    public long countAuthors() {
        return authorRepository.count();
    }
    
    @Override
    public List<Author> findMostProlificAuthors(int limit) {
        return authorRepository.findAll().stream()
                .sorted((a1, a2) -> Integer.compare(a2.getBooks().size(), a1.getBooks().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Author> findByBookCount(int minBooks) {
        return authorRepository.findAll().stream()
                .filter(author -> author.getBooks().size() >= minBooks)
                .collect(Collectors.toList());
    }
}
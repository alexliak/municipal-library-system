package com.municipality.library.repository;

import com.municipality.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByGenre(String genre);
    List<Book> findByGenreContainingIgnoreCase(String genre);
    List<Book> findByTitleContainingIgnoreCaseOrGenreContainingIgnoreCase(String title, String genre);
    List<Book> findByAvailableCopiesGreaterThan(int copies);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.authorId = :authorId")
    List<Book> findByAuthorsAuthorId(@Param("authorId") Integer authorId);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Book> findByAuthorsNameContainingIgnoreCase(@Param("name") String name);
    
    List<Book> findByPublicationDateBetween(LocalDate start, LocalDate end);
    
    Long countByAvailableCopiesGreaterThan(int copies);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchBooksAndAuthors(@Param("query") String query);
    
    List<Book> findByGenreIgnoreCase(String genre);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.authorId = :authorId")
    List<Book> findByAuthors_AuthorId(@Param("authorId") Long authorId);
    
    List<Book> findByAvailableCopiesLessThanEqual(int threshold);
    
    @Query("SELECT b FROM Book b LEFT JOIN b.loans l GROUP BY b ORDER BY COUNT(l) DESC")
    List<Book> findAllBooksOrderedByLoanCount();
    
    default List<Book> findMostBorrowedBooks(int limit) {
        List<Book> allBooks = findAllBooksOrderedByLoanCount();
        return allBooks.size() > limit ? allBooks.subList(0, limit) : allBooks;
    }
    
    Long countByAvailableCopies(int copies);
    
    @Query("SELECT SUM(b.totalCopies) FROM Book b")
    Long sumTotalCopies();
    
    @Query("SELECT SUM(b.availableCopies) FROM Book b")
    Long sumAvailableCopies();
    
    @Query("SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre ORDER BY COUNT(b) DESC")
    List<Object[]> countBooksByGenre();
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.createdAt > :date")
    Long countBooksAddedAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT b FROM Book b WHERE b.createdAt > :date ORDER BY b.createdAt DESC")
    List<Book> findBooksAddedAfter(@Param("date") LocalDateTime date);
    
    // Additional queries for reporting
    Long countByCreatedAtAfter(LocalDateTime date);
    
    List<Book> findByCreatedAtAfter(LocalDateTime date);
    
    // Add missing method
    Long countByAvailableCopiesLessThanEqual(int threshold);
}
package com.municipality.library.repository;

import com.municipality.library.entity.BookRating;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {
    
    // Find rating by user and book
    Optional<BookRating> findByUserAndBook(User user, Book book);
    
    // Find all ratings for a book
    List<BookRating> findByBook(Book book);
    
    // Find all ratings by a user
    List<BookRating> findByUser(User user);
    
    // Count ratings for a book
    Integer countByBook_Isbn(String isbn);
    
    // Get average rating for a book
    @Query("SELECT AVG(r.rating) FROM BookRating r WHERE r.book.isbn = :isbn")
    Double getAverageRatingForBook(@Param("isbn") String isbn);
    
    // Get overall average rating
    @Query("SELECT AVG(r.rating) FROM BookRating r")
    Double getAverageRating();
    
    // Find books with average rating greater than X
    @Query("SELECT r.book, AVG(r.rating) as avgRating, COUNT(r) as ratingCount " +
           "FROM BookRating r " +
           "GROUP BY r.book " +
           "HAVING AVG(r.rating) >= :minRating " +
           "ORDER BY avgRating DESC")
    List<Object[]> findBooksWithAverageRatingGreaterThan(@Param("minRating") double minRating);
    
    // Find top rated books
    @Query("SELECT r.book, AVG(r.rating) as avgRating, COUNT(r) as ratingCount " +
           "FROM BookRating r " +
           "GROUP BY r.book " +
           "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedBooks();
}

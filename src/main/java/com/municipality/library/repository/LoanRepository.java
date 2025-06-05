package com.municipality.library.repository;

import com.municipality.library.entity.Book;
import com.municipality.library.entity.Loan;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
    List<Loan> findByUser(User user);
    List<Loan> findByBook(Book book);
    List<Loan> findByUserAndStatus(User user, LoanStatus status);
    List<Loan> findByUserAndStatusAndDueDateBefore(User user, LoanStatus status, LocalDateTime date);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime date);
    List<Loan> findByStatusAndDueDateBetween(LoanStatus status, LocalDateTime start, LocalDateTime end);
    
    boolean existsByUserAndBookAndStatus(User user, Book book, LoanStatus status);
    
    int countByUserAndStatus(User user, LoanStatus status);
    Long countByStatus(LoanStatus status);
    
    @Query("SELECT l.book, COUNT(l) as borrowCount FROM Loan l GROUP BY l.book ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedBooks();
    
    @Query("SELECT l.user, COUNT(l) as loanCount FROM Loan l WHERE l.status = 'ACTIVE' GROUP BY l.user ORDER BY loanCount DESC")
    List<Object[]> findMostActiveMembers();
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    Long countActiveLoans();
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :currentDate")
    Long countOverdueLoans(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE MONTH(l.loanDate) = MONTH(CURRENT_DATE()) AND YEAR(l.loanDate) = YEAR(CURRENT_DATE())")
    Long countLoansThisMonth();
    
    Long countByLoanDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.loanDate BETWEEN :start AND :end AND l.returnDate IS NOT NULL AND l.returnDate <= l.dueDate")
    Long countReturnedOnTime(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.loanDate BETWEEN :start AND :end AND l.returnDate IS NOT NULL AND l.returnDate > l.dueDate")
    Long countReturnedLate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT l.user, COUNT(l) as count FROM Loan l GROUP BY l.user ORDER BY count DESC")
    List<Object[]> findAllBorrowersOrderedByLoanCount();
    
    default List<Object[]> findTopBorrowers(int limit) {
        List<Object[]> allBorrowers = findAllBorrowersOrderedByLoanCount();
        return allBorrowers.size() > limit ? allBorrowers.subList(0, limit) : allBorrowers;
    }
    
    @Query("SELECT COUNT(DISTINCT l.user) FROM Loan l WHERE l.status = 'ACTIVE'")
    Long countDistinctUsersWithActiveLoans();
    
    @Query("SELECT COUNT(DISTINCT l.user) FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :currentDate")
    Long countDistinctUsersWithOverdueLoans(@Param("currentDate") LocalDate currentDate);
    
    List<Loan> findTop5ByOrderByLoanDateDesc();
    
    @Query("SELECT l FROM Loan l WHERE l.returnDate IS NOT NULL ORDER BY l.returnDate DESC")
    List<Loan> findTop5ReturnedOrderByReturnDateDesc();
    
    @Query("SELECT MONTH(l.loanDate) as month, COUNT(l) as count FROM Loan l WHERE YEAR(l.loanDate) = YEAR(CURRENT_DATE()) GROUP BY MONTH(l.loanDate)")
    List<Object[]> getLoansPerMonth();
    
    // Additional queries for reporting
    Long countByStatusAndDueDateBefore(LoanStatus status, LocalDateTime date);
    
    Integer countByBookAndStatus(Book book, LoanStatus status);
    
    int countByUserAndStatusAndDueDateBefore(User user, LoanStatus status, LocalDateTime date);
    
    @Query("SELECT COUNT(DISTINCT l.user) FROM Loan l WHERE l.loanDate > :date")
    Long countDistinctUsersWithLoansAfter(@Param("date") LocalDateTime date);
    
    Long countByLoanDateAfter(LocalDateTime date);
    
    Long countByReturnDateAfter(LocalDateTime date);
    
    // New methods for ReportService
    Long countByBook(Book book);
    
    // New methods for member dashboard
    int countByUserAndLoanDateAfter(User user, LocalDateTime date);
    
    @Query("SELECT l.book.genre, COUNT(l) FROM Loan l WHERE l.user.userId = :userId GROUP BY l.book.genre ORDER BY COUNT(l) DESC")
    List<Object[]> findFavoriteGenreForUser(@Param("userId") Integer userId);
}

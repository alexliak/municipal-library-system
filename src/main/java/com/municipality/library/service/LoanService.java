package com.municipality.library.service;

import com.municipality.library.entity.Loan;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.entity.User;
import com.municipality.library.entity.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    
    // CRUD operations
    Loan save(Loan loan);
    Optional<Loan> findById(Integer id);
    List<Loan> findAll();
    void delete(Loan loan);
    
    // Core functionality
    Loan checkoutBook(User user, Book book, LocalDateTime dueDate);
    Loan checkoutBook(User user, Book book, LocalDateTime dueDate, String notes);
    Loan returnBook(Integer loanId);
    List<Loan> findByUser(User user);
    List<Loan> findByBook(Book book);
    List<Loan> findByStatus(LoanStatus status);
    
    // User-specific queries
    List<Loan> findActiveLoansForUser(Integer userId);
    List<Loan> findLoanHistoryForUser(Integer userId);
    boolean hasUserBorrowedBook(Integer userId, String bookIsbn);
    int countActiveLoansForUser(Integer userId);
    
    // Overdue management
    List<Loan> findOverdueLoans();
    List<Loan> findLoansDueSoon(int days);
    void markAsOverdue(Integer loanId);
    
    // Statistics
    long countActiveLoans();
    long countOverdueLoans();
    List<Object[]> getMostBorrowedBooks(int limit);
    List<Object[]> getMostActiveUsers(int limit);
    double getAverageLoanDuration();
    
    // Member dashboard specific methods
    int countOverdueLoansForUser(Integer userId);
    int countReturnedLoansForUser(Integer userId);
    int countLoansThisMonthForUser(Integer userId);
    int countLoansThisYearForUser(Integer userId);
    String getFavoriteGenreForUser(Integer userId);
}
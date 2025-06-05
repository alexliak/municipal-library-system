package com.municipality.library.service.impl;

import com.municipality.library.entity.Loan;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.User;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.repository.LoanRepository;
import com.municipality.library.repository.BookRepository;
import com.municipality.library.repository.UserRepository;
import com.municipality.library.service.BookService;
import com.municipality.library.service.LoanService;
import com.municipality.library.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private EmailService emailService;
    
    @Override
    public Loan save(Loan loan) {
        return loanRepository.save(loan);
    }
    
    @Override
    public Optional<Loan> findById(Integer id) {
        return loanRepository.findById(id);
    }
    
    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }
    
    @Override
    public void delete(Loan loan) {
        loanRepository.delete(loan);
    }
    
    @Override
    public Loan checkoutBook(User user, Book book, LocalDateTime dueDate) {
        return checkoutBook(user, book, dueDate, null);
    }
    
    @Override
    public Loan checkoutBook(User user, Book book, LocalDateTime dueDate, String notes) {
        // Validate book availability
        if (!bookService.isAvailable(book.getIsbn())) {
            throw new RuntimeException("Book is not available for checkout");
        }
        
        // Check if user has overdue books
        List<Loan> overdueLoans = findOverdueLoansForUser(user.getUserId());
        if (!overdueLoans.isEmpty()) {
            throw new RuntimeException("User has overdue books and cannot checkout new books");
        }
        
        // Create new loan
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDateTime.now());
        loan.setDueDate(dueDate != null ? dueDate : LocalDateTime.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setNotes(notes);
        
        // Update book availability
        bookService.decrementAvailableCopies(book.getIsbn());
        
        Loan savedLoan = loanRepository.save(loan);
        
        // Send email confirmation
        emailService.sendLoanConfirmation(user, savedLoan);
        
        return savedLoan;
    }
    
    @Override
    public Loan returnBook(Integer loanId) {
        Loan loan = findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new RuntimeException("Book has already been returned");
        }
        
        loan.setReturnDate(LocalDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        
        // Update book availability
        bookService.incrementAvailableCopies(loan.getBook().getIsbn());
        
        Loan returnedLoan = loanRepository.save(loan);
        
        // Send return confirmation
        emailService.sendReturnConfirmation(loan.getUser(), returnedLoan);
        
        return returnedLoan;
    }
    
    @Override
    public List<Loan> findByUser(User user) {
        return loanRepository.findByUser(user);
    }
    
    @Override
    public List<Loan> findByBook(Book book) {
        return loanRepository.findByBook(book);
    }
    
    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        return loanRepository.findByStatus(status);
    }
    
    @Override
    public List<Loan> findActiveLoansForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.findByUserAndStatus(user, LoanStatus.ACTIVE);
    }
    
    @Override
    public List<Loan> findLoanHistoryForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.findByUser(user);
    }
    
    @Override
    public boolean hasUserBorrowedBook(Integer userId, String bookIsbn) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + bookIsbn));
        
        return loanRepository.existsByUserAndBookAndStatus(user, book, LoanStatus.ACTIVE);
    }
    
    @Override
    public int countActiveLoansForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.countByUserAndStatus(user, LoanStatus.ACTIVE);
    }
    
    @Override
    public List<Loan> findOverdueLoans() {
        return loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDateTime.now());
    }
    
    @Override
    public List<Loan> findLoansDueSoon(int days) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(days);
        return loanRepository.findByStatusAndDueDateBetween(
                LoanStatus.ACTIVE, 
                LocalDateTime.now(), 
                targetDate
        );
    }
    
    @Override
    public void markAsOverdue(Integer loanId) {
        Loan loan = findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        
        if (loan.getStatus() == LoanStatus.ACTIVE && loan.getDueDate().isBefore(LocalDateTime.now())) {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
        }
    }
    
    @Override
    public long countActiveLoans() {
        return loanRepository.countByStatus(LoanStatus.ACTIVE);
    }
    
    @Override
    public long countOverdueLoans() {
        return loanRepository.countByStatus(LoanStatus.OVERDUE);
    }
    
    @Override
    public List<Object[]> getMostBorrowedBooks(int limit) {
        return loanRepository.findMostBorrowedBooks().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Object[]> getMostActiveUsers(int limit) {
        return loanRepository.findMostActiveMembers().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public double getAverageLoanDuration() {
        List<Loan> returnedLoans = loanRepository.findByStatus(LoanStatus.RETURNED);
        if (returnedLoans.isEmpty()) {
            return 0.0;
        }
        
        double totalDays = returnedLoans.stream()
                .filter(loan -> loan.getReturnDate() != null)
                .mapToLong(loan -> ChronoUnit.DAYS.between(loan.getLoanDate(), loan.getReturnDate()))
                .sum();
        
        return totalDays / returnedLoans.size();
    }
    
    // Helper method
    private List<Loan> findOverdueLoansForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.findByUserAndStatusAndDueDateBefore(user, LoanStatus.ACTIVE, LocalDateTime.now());
    }
    
    @Override
    public int countOverdueLoansForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.countByUserAndStatusAndDueDateBefore(user, LoanStatus.ACTIVE, LocalDateTime.now());
    }
    
    @Override
    public int countReturnedLoansForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return loanRepository.countByUserAndStatus(user, LoanStatus.RETURNED);
    }
    
    @Override
    public int countLoansThisMonthForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return loanRepository.countByUserAndLoanDateAfter(user, startOfMonth);
    }
    
    @Override
    public int countLoansThisYearForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        return loanRepository.countByUserAndLoanDateAfter(user, startOfYear);
    }
    
    @Override
    public String getFavoriteGenreForUser(Integer userId) {
        List<Object[]> genreCounts = loanRepository.findFavoriteGenreForUser(userId);
        if (!genreCounts.isEmpty() && genreCounts.get(0)[0] != null) {
            return (String) genreCounts.get(0)[0];
        }
        return "No favorite genre yet";
    }
}
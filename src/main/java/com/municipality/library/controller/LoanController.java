package com.municipality.library.controller;

import com.municipality.library.entity.Loan;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.User;
import com.municipality.library.service.LoanService;
import com.municipality.library.service.BookService;
import com.municipality.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/loans")
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    // Show general checkout form (for librarians/admins)
    @GetMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String showGeneralCheckoutForm(ModelMap model) {
        List<Book> availableBooks = bookService.findAvailableBooks();
        List<User> members = userService.findUsersByRole("MEMBER");
        
        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("users", members);
        return "loans/checkout";
    }
    
    // Show checkout form for specific book (for members)
    @GetMapping("/checkout/{isbn}")
    @PreAuthorize("hasRole('MEMBER')")
    public String showCheckoutForm(@PathVariable String isbn, 
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  ModelMap model) {
        Optional<Book> bookOpt = bookService.findById(isbn);
        if (!bookOpt.isPresent()) {
            return "redirect:/books";
        }
        
        Book book = bookOpt.get();
        if (!bookService.isAvailable(isbn)) {
            model.addAttribute("error", "This book is not available for checkout");
            return "redirect:/books/" + isbn;
        }
        
        // Get user's current loan count
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (userOpt.isPresent()) {
            int activeLoans = loanService.countActiveLoansForUser(userOpt.get().getUserId());
            model.addAttribute("activeLoansCount", activeLoans);
        }
        
        model.addAttribute("book", book);
        return "loans/checkout";
    }
    
    // Process general checkout (for librarians/admins)
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String checkoutBookForUser(@RequestParam String bookIsbn,
                                     @RequestParam Integer userId,
                                     @RequestParam(required = false) String dueDate,
                                     @RequestParam(required = false) String notes,
                                     RedirectAttributes redirectAttributes) {
        try {
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found");
                return "redirect:/loans/checkout";
            }
            
            Optional<Book> bookOpt = bookService.findById(bookIsbn);
            if (!bookOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                return "redirect:/loans/checkout";
            }
            
            User user = userOpt.get();
            Book book = bookOpt.get();
            
            // Parse due date or use default (14 days)
            LocalDateTime dueDatetime;
            if (dueDate != null && !dueDate.isEmpty()) {
                dueDatetime = LocalDateTime.parse(dueDate + "T23:59:59");
            } else {
                dueDatetime = LocalDateTime.now().plusDays(14);
            }
            
            Loan loan = loanService.checkoutBook(user, book, dueDatetime, notes);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book '" + book.getTitle() + "' checked out to " + user.getFullName() + "! Due date: " + loan.getDueDate().toLocalDate());
            return "redirect:/loans";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/loans/checkout";
        }
    }
    
    // Process checkout for member
    @PostMapping("/checkout-member")
    @PreAuthorize("hasRole('MEMBER')")
    public String checkoutBook(@RequestParam String isbn,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        System.out.println("=== MEMBER CHECKOUT DEBUG ===");
        System.out.println("ISBN: " + isbn);
        System.out.println("User: " + userDetails.getUsername());
        try {
            Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
            if (!userOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found");
                return "redirect:/books";
            }
            
            Optional<Book> bookOpt = bookService.findById(isbn);
            if (!bookOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found");
                return "redirect:/books";
            }
            
            User user = userOpt.get();
            Book book = bookOpt.get();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(14);
            
            Loan loan = loanService.checkoutBook(user, book, dueDate);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book checked out successfully! Due date: " + loan.getDueDate().toLocalDate());
            return "redirect:/loans/my";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/books/" + isbn;
        }
    }
    
    // View my loans (for members)
    @GetMapping("/my")
    @PreAuthorize("hasRole('MEMBER')")
    public String myLoans(@AuthenticationPrincipal UserDetails userDetails, ModelMap model) {
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (!userOpt.isPresent()) {
            return "redirect:/";
        }
        
        User user = userOpt.get();
        List<Loan> loans = loanService.findLoanHistoryForUser(user.getUserId());
        List<Loan> activeLoans = loanService.findActiveLoansForUser(user.getUserId());
        
        model.addAttribute("loans", loans);
        model.addAttribute("activeLoans", activeLoans);
        return "loans/my-loans";
    }
    
    // View all loans (for librarians and admins)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String allLoans(ModelMap model) {
        List<Loan> loans = loanService.findAll();
        List<Loan> overdueLoans = loanService.findOverdueLoans();
        
        model.addAttribute("loans", loans);
        model.addAttribute("overdueLoans", overdueLoans);
        model.addAttribute("totalActive", loanService.countActiveLoans());
        model.addAttribute("totalOverdue", loanService.countOverdueLoans());
        return "loans/list";
    }
    
    // Return book
    @PostMapping("/return/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String returnBook(@PathVariable Integer loanId, RedirectAttributes redirectAttributes) {
        try {
            Loan loan = loanService.returnBook(loanId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book returned successfully by " + loan.getUser().getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }
    
    // Mark as overdue
    @PostMapping("/overdue/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String markAsOverdue(@PathVariable Integer loanId, RedirectAttributes redirectAttributes) {
        try {
            loanService.markAsOverdue(loanId);
            redirectAttributes.addFlashAttribute("warningMessage", "Loan marked as overdue");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }
    
    // View overdue loans
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String overdueLoans(ModelMap model) {
        List<Loan> overdueLoans = loanService.findOverdueLoans();
        model.addAttribute("loans", overdueLoans);
        model.addAttribute("pageTitle", "Overdue Loans");
        return "loans/list";
    }
    
    // View loans due soon
    @GetMapping("/due-soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String loansDueSoon(@RequestParam(defaultValue = "3") int days, ModelMap model) {
        List<Loan> loansDueSoon = loanService.findLoansDueSoon(days);
        model.addAttribute("loans", loansDueSoon);
        model.addAttribute("pageTitle", "Loans Due in " + days + " Days");
        model.addAttribute("days", days);
        return "loans/list";
    }
}
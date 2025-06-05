package com.municipality.library.controller;

import com.municipality.library.entity.Loan;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.User;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.service.interfaces.LoanService;
import com.municipality.library.service.interfaces.BookService;
import com.municipality.library.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/loans")
public class LoanController {
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    // Show checkout form
    @GetMapping("/checkout/{isbn}")
    @PreAuthorize("hasRole('MEMBER')")
    public String showCheckoutForm(@PathVariable String isbn, ModelMap model) {
        Book book = bookService.findBookByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (book.getAvailableCopies() <= 0) {
            model.addAttribute("error", "This book is not available for checkout");
            return "redirect:/books/" + isbn;
        }
        
        model.addAttribute("book", book);
        return "loans/checkout";
    }
    
    // Process checkout
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('MEMBER')")
    public String checkoutBook(@RequestParam String isbn,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            Loan loan = loanService.checkoutBook(isbn, user.getUserId());
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book checked out successfully! Due date: " + loan.getDueDate());
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
        User user = userService.findByUsername(userDetails.getUsername());
        List<Loan> loans = loanService.findLoansByUser(user.getUserId());
        List<Loan> activeLoans = loanService.findActiveLoansByUser(user.getUserId());
        
        model.addAttribute("loans", loans);
        model.addAttribute("activeLoans", activeLoans);
        return "loans/my-loans";
    }
    
    // View all loans (for librarians and admins)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String allLoans(ModelMap model) {
        List<Loan> loans = loanService.findAllLoans();
        List<Loan> overdueLoans = loanService.findOverdueLoans();
        
        model.addAttribute("loans", loans);
        model.addAttribute("overdueLoans", overdueLoans);
        model.addAttribute("totalActive", loanService.getTotalActiveLoans());
        model.addAttribute("totalOverdue", loanService.getTotalOverdueLoans());
        return "loans/list";
    }
    
    // Return book
    @PostMapping("/return/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String returnBook(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        try {
            Loan loan = loanService.returnBook(loanId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book returned successfully by " + loan.getUser().getUsername());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/loans";
    }
    
    // Mark as lost
    @PostMapping("/lost/{loanId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public String markAsLost(@PathVariable Long loanId, RedirectAttributes redirectAttributes) {
        try {
            Loan loan = loanService.markAsLost(loanId);
            redirectAttributes.addFlashAttribute("warningMessage", 
                "Book marked as lost. User: " + loan.getUser().getUsername());
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
    
    // Update overdue status (scheduled task endpoint)
    @PostMapping("/update-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateOverdueStatus(RedirectAttributes redirectAttributes) {
        loanService.updateOverdueStatus();
        redirectAttributes.addFlashAttribute("successMessage", "Overdue status updated successfully");
        return "redirect:/loans";
    }
}

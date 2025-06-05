package com.municipality.library.controller;

import com.municipality.library.dto.DashboardStats;
import com.municipality.library.dto.BookStatistics;
import com.municipality.library.dto.MemberStatistics;
import com.municipality.library.entity.User;
import com.municipality.library.service.ReportService;
import com.municipality.library.service.UserService;
import com.municipality.library.service.LoanService;
import com.municipality.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/")
    public String home(Principal principal) {
        if (principal != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(ModelMap model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
            
            // Check if user is a member and redirect to member dashboard
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isMember = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
            
            boolean isAdminOrLibrarian = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                              a.getAuthority().equals("ROLE_LIBRARIAN"));
            
            // If user is ONLY a member (not admin or librarian), show member dashboard
            if (isMember && !isAdminOrLibrarian) {
                // Get member-specific data
                Optional<User> userOpt = userService.findByUsername(principal.getName());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Get member stats
                    model.addAttribute("myActiveLoans", loanService.countActiveLoansForUser(user.getUserId()));
                    model.addAttribute("myOverdueLoans", loanService.countOverdueLoansForUser(user.getUserId()));
                    model.addAttribute("totalBooksRead", loanService.countReturnedLoansForUser(user.getUserId()));
                    model.addAttribute("availableBooks", bookService.countAvailableBooks());
                    
                    // Get current loans
                    model.addAttribute("currentLoans", loanService.findActiveLoansForUser(user.getUserId()));
                    
                    // Get recommended books (for now, just get popular books)
                    model.addAttribute("recommendedBooks", bookService.findTopRatedBooks(4));
                    
                    // Reading stats
                    model.addAttribute("booksThisMonth", loanService.countLoansThisMonthForUser(user.getUserId()));
                    model.addAttribute("booksThisYear", loanService.countLoansThisYearForUser(user.getUserId()));
                    model.addAttribute("favoriteGenre", loanService.getFavoriteGenreForUser(user.getUserId()));
                    
                    // Member since
                    model.addAttribute("memberSince", user.getCreatedAt() != null ? 
                        user.getCreatedAt().getYear() : "2024");
                }
                
                return "member-dashboard";
            }
        }
        
        // For admin and librarian users, show full dashboard
        // Get dashboard statistics
        DashboardStats stats = reportService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        
        // Get most borrowed books (top 5 for dashboard)
        List<BookStatistics> mostBorrowed = reportService.getMostBorrowedBooks(5);
        model.addAttribute("mostBorrowedBooks", mostBorrowed);
        
        // Get most active members (top 5 for dashboard)
        List<MemberStatistics> mostActive = reportService.getMostActiveMembers(5);
        model.addAttribute("mostActiveMembers", mostActive);
        
        // Get genre statistics for pie chart
        Map<String, Integer> genreStats = reportService.getBooksByGenreStatistics();
        model.addAttribute("genreStats", genreStats);
        
        return "dashboard";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
    
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Application is running! Version 2";
    }
    
    @GetMapping("/profile")
    public String profileRedirect() {
        return "redirect:/users/profile";
    }
    
    @GetMapping("/settings")
    public String settings(ModelMap model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "settings";
    }
}

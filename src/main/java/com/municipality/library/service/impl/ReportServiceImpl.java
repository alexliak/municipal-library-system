package com.municipality.library.service.impl;

import com.municipality.library.dto.DashboardStats;
import com.municipality.library.dto.BookStatistics;
import com.municipality.library.dto.MemberStatistics;
import com.municipality.library.dto.InventoryReport;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.Loan;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.entity.User;
import com.municipality.library.repository.BookRepository;
import com.municipality.library.repository.LoanRepository;
import com.municipality.library.repository.UserRepository;
import com.municipality.library.repository.AuthorRepository;
import com.municipality.library.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final AuthorRepository authorRepository;
    
    @Override
    public DashboardStats getDashboardStatistics() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalBooks(bookRepository.count());
        stats.setTotalMembers(userRepository.countByRolesName("ROLE_MEMBER"));
        stats.setActiveLoans(loanRepository.countByStatus(LoanStatus.ACTIVE));
        stats.setOverdueLoans(loanRepository.countByStatusAndDueDateBefore(
            LoanStatus.ACTIVE, LocalDateTime.now()));
        stats.setTotalAuthors(authorRepository.count());
        
        // Books added this month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        stats.setNewBooksThisMonth(bookRepository.countBooksAddedAfter(startOfMonth));
        
        // Members joined this month
        stats.setNewMembersThisMonth(userRepository.countUsersCreatedAfter(startOfMonth));
        
        // Average loans per member
        long totalLoans = loanRepository.count();
        long totalMembers = stats.getTotalMembers();
        stats.setAverageLoansPerMember(totalMembers > 0 ? 
            (double) totalLoans / totalMembers : 0.0);
        
        return stats;
    }
    
    @Override
    public List<BookStatistics> getMostBorrowedBooks(int limit) {
        List<Object[]> results = loanRepository.findMostBorrowedBooks();
        
        return results.stream()
            .limit(limit)
            .map(row -> {
                Book book = (Book) row[0];
                Long loanCount = (Long) row[1];
                
                BookStatistics stats = new BookStatistics();
                stats.setIsbn(book.getIsbn());
                stats.setTitle(book.getTitle());
                stats.setTotalCopies(book.getTotalCopies());
                stats.setAvailableCopies(book.getAvailableCopies());
                stats.setLoanCount(loanCount);
                stats.setGenre(book.getGenre());
                stats.setCreatedAt(book.getCreatedAt());
                
                // Set authors string
                String authors = book.getAuthors().stream()
                    .map(a -> a.getName())
                    .collect(Collectors.joining(", "));
                stats.setAuthorsString(authors);
                
                return stats;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookStatistics> getBooksByRating(int minRating) {
        // Since we don't have ratings implemented yet, return empty list
        return new ArrayList<>();
    }
    
    @Override
    public Map<String, Integer> getBooksByGenreStatistics() {
        List<Object[]> results = bookRepository.countBooksByGenre();
        Map<String, Integer> genreStats = new LinkedHashMap<>();
        
        for (Object[] row : results) {
            String genre = (String) row[0];
            Long count = (Long) row[1];
            genreStats.put(genre, count.intValue());
        }
        
        return genreStats;
    }
    
    @Override
    public List<MemberStatistics> getMostActiveMembers(int limit) {
        List<Object[]> results = loanRepository.findMostActiveMembers();
        
        return results.stream()
            .limit(limit)
            .map(row -> {
                User user = (User) row[0];
                Long loanCount = (Long) row[1];
                
                MemberStatistics stats = new MemberStatistics();
                stats.setMemberId(user.getUserId());
                stats.setMemberName(user.getFullName());
                stats.setUsername(user.getUsername());
                stats.setEmail(user.getEmail());
                stats.setTotalLoans(loanCount);
                stats.setJoinDate(user.getCreatedAt() != null ? 
                    user.getCreatedAt().toLocalDate() : null);
                
                // Get active loans count
                long activeLoans = loanRepository.countByUserAndStatus(user, LoanStatus.ACTIVE);
                stats.setActiveLoans(activeLoans);
                
                // Get overdue loans count
                long overdueLoans = loanRepository.countByUserAndStatusAndDueDateBefore(
                    user, LoanStatus.ACTIVE, LocalDateTime.now());
                stats.setOverdueLoans(overdueLoans);
                
                return stats;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public long getTotalActiveMembers() {
        return userRepository.countByEnabledAndRolesName(true, "ROLE_MEMBER");
    }
    
    @Override
    public Map<String, Integer> getMembershipGrowth(int months) {
        Map<String, Integer> growth = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        
        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            
            int count = userRepository.countUsersCreatedBetween(
                monthStart.atStartOfDay(), 
                monthEnd.atTime(23, 59, 59)
            );
            
            String monthKey = monthStart.getYear() + "-" + 
                String.format("%02d", monthStart.getMonthValue());
            growth.put(monthKey, count);
        }
        
        return growth;
    }
    
    @Override
    public InventoryReport getInventoryStatus() {
        long totalBooks = bookRepository.sumTotalCopies();
        long availableBooks = bookRepository.sumAvailableCopies();
        long uniqueTitles = bookRepository.count();
        
        return new InventoryReport(totalBooks, availableBooks, uniqueTitles);
    }
    
    @Override
    public List<BookStatistics> getLowStockBooks(int threshold) {
        List<Book> lowStockBooks = bookRepository.findByAvailableCopiesLessThanEqual(threshold);
        
        return lowStockBooks.stream()
            .map(book -> {
                BookStatistics stats = new BookStatistics();
                stats.setIsbn(book.getIsbn());
                stats.setTitle(book.getTitle());
                stats.setTotalCopies(book.getTotalCopies());
                stats.setAvailableCopies(book.getAvailableCopies());
                
                // Get loan count for this book
                Long loanCount = loanRepository.countByBook(book);
                stats.setLoanCount(loanCount);
                
                return stats;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<BookStatistics> getOverdueBooks() {
        List<Loan> overdueLoans = loanRepository.findByStatusAndDueDateBefore(
            LoanStatus.ACTIVE, LocalDateTime.now());
        
        return overdueLoans.stream()
            .map(loan -> {
                BookStatistics stats = new BookStatistics();
                stats.setIsbn(loan.getBook().getIsbn());
                stats.setTitle(loan.getBook().getTitle());
                stats.setBorrowerName(loan.getUser().getFullName());
                stats.setDueDate(loan.getDueDate());
                
                long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
                stats.setDaysOverdue(daysOverdue);
                
                return stats;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public ByteArrayOutputStream exportDashboardToPDF() {
        // Simple implementation - in real app, use a PDF library like iText
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // PDF generation code here
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream exportInventoryToExcel() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory Report");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ISBN");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Authors");
            headerRow.createCell(3).setCellValue("Genre");
            headerRow.createCell(4).setCellValue("Total Copies");
            headerRow.createCell(5).setCellValue("Available Copies");
            headerRow.createCell(6).setCellValue("Borrowed Copies");
            
            // Add data
            List<Book> books = bookRepository.findAll();
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getIsbn());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(
                    book.getAuthors().stream()
                        .map(a -> a.getName())
                        .collect(Collectors.joining(", "))
                );
                row.createCell(3).setCellValue(book.getGenre());
                row.createCell(4).setCellValue(book.getTotalCopies());
                row.createCell(5).setCellValue(book.getAvailableCopies());
                row.createCell(6).setCellValue(book.getTotalCopies() - book.getAvailableCopies());
            }
            
            // Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream exportMemberReportToExcel() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Member Statistics");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Member ID");
            headerRow.createCell(1).setCellValue("Username");
            headerRow.createCell(2).setCellValue("Full Name");
            headerRow.createCell(3).setCellValue("Email");
            headerRow.createCell(4).setCellValue("Total Loans");
            headerRow.createCell(5).setCellValue("Active Loans");
            headerRow.createCell(6).setCellValue("Join Date");
            
            // Add data
            List<MemberStatistics> members = getMostActiveMembers(1000);
            int rowNum = 1;
            for (MemberStatistics member : members) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(member.getMemberId());
                row.createCell(1).setCellValue(member.getUsername());
                row.createCell(2).setCellValue(member.getMemberName());
                row.createCell(3).setCellValue(member.getEmail());
                row.createCell(4).setCellValue(member.getTotalLoans());
                row.createCell(5).setCellValue(member.getActiveLoans());
                row.createCell(6).setCellValue(member.getJoinDate() != null ? 
                    member.getJoinDate().toString() : "");
            }
            
            // Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return baos;
    }
    
    @Override
    public ByteArrayOutputStream exportBookStatisticsToExcel() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Book Statistics");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ISBN");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Genre");
            headerRow.createCell(3).setCellValue("Times Borrowed");
            headerRow.createCell(4).setCellValue("Available/Total");
            
            // Add data
            List<BookStatistics> books = getMostBorrowedBooks(1000);
            int rowNum = 1;
            for (BookStatistics book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getIsbn());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getGenre());
                row.createCell(3).setCellValue(book.getLoanCount());
                row.createCell(4).setCellValue(book.getAvailableCopies() + "/" + book.getTotalCopies());
            }
            
            // Auto-size columns
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(baos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return baos;
    }
    
    @Override
    public List<String> checkDueDateAlerts(int daysAhead) {
        List<String> alerts = new ArrayList<>();
        LocalDateTime targetDate = LocalDateTime.now().plusDays(daysAhead);
        
        List<Loan> loansDueSoon = loanRepository.findByStatusAndDueDateBetween(
            LoanStatus.ACTIVE, LocalDateTime.now(), targetDate);
        
        for (Loan loan : loansDueSoon) {
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDateTime.now(), loan.getDueDate());
            String alert = String.format("%s has '%s' due in %d days (Due: %s)",
                loan.getUser().getFullName(),
                loan.getBook().getTitle(),
                daysUntilDue,
                loan.getDueDate().toLocalDate()
            );
            alerts.add(alert);
        }
        
        return alerts;
    }
    
    @Override
    public List<String> checkOverdueNotifications() {
        List<String> notifications = new ArrayList<>();
        
        List<Loan> overdueLoans = loanRepository.findByStatusAndDueDateBefore(
            LoanStatus.ACTIVE, LocalDateTime.now());
        
        for (Loan loan : overdueLoans) {
            long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDateTime.now());
            String notification = String.format("%s has '%s' overdue by %d days (Was due: %s)",
                loan.getUser().getFullName(),
                loan.getBook().getTitle(),
                daysOverdue,
                loan.getDueDate().toLocalDate()
            );
            notifications.add(notification);
        }
        
        return notifications;
    }
    
    @Override
    public List<String> getNewArrivalsThisWeek() {
        List<String> arrivals = new ArrayList<>();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        
        List<Book> newBooks = bookRepository.findBooksAddedAfter(oneWeekAgo);
        
        for (Book book : newBooks) {
            String arrival = String.format("New Book: '%s' by %s (ISBN: %s)",
                book.getTitle(),
                book.getAuthors().stream()
                    .map(a -> a.getName())
                    .collect(Collectors.joining(", ")),
                book.getIsbn()
            );
            arrivals.add(arrival);
        }
        
        return arrivals;
    }
}
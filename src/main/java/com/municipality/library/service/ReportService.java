package com.municipality.library.service;

import com.municipality.library.dto.DashboardStats;
import com.municipality.library.dto.BookStatistics;
import com.municipality.library.dto.MemberStatistics;
import com.municipality.library.dto.InventoryReport;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    
    // Dashboard Statistics
    DashboardStats getDashboardStatistics();
    
    // Book Statistics
    List<BookStatistics> getMostBorrowedBooks(int limit);
    List<BookStatistics> getBooksByRating(int minRating);
    Map<String, Integer> getBooksByGenreStatistics();
    
    // Member Statistics
    List<MemberStatistics> getMostActiveMembers(int limit);
    long getTotalActiveMembers();
    Map<String, Integer> getMembershipGrowth(int months);
    
    // Inventory Reports
    InventoryReport getInventoryStatus();
    List<BookStatistics> getLowStockBooks(int threshold);
    List<BookStatistics> getOverdueBooks();
    
    // Export Functions
    ByteArrayOutputStream exportDashboardToPDF();
    ByteArrayOutputStream exportInventoryToExcel();
    ByteArrayOutputStream exportMemberReportToExcel();
    ByteArrayOutputStream exportBookStatisticsToExcel();
    
    // Notification Checks
    List<String> checkDueDateAlerts(int daysAhead);
    List<String> checkOverdueNotifications();
    List<String> getNewArrivalsThisWeek();
}

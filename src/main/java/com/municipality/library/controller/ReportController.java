package com.municipality.library.controller;

import com.municipality.library.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @GetMapping("/dashboard")
    public String dashboardReport(ModelMap model) {
        model.addAttribute("stats", reportService.getDashboardStatistics());
        return "reports/dashboard";
    }
    
    @GetMapping("/members")
    public String membersReport(ModelMap model) {
        model.addAttribute("members", reportService.getMostActiveMembers(100));
        model.addAttribute("totalMembers", reportService.getTotalActiveMembers());
        model.addAttribute("membershipGrowth", reportService.getMembershipGrowth(6));
        return "reports/members";
    }
    
    @GetMapping("/books")
    public String booksReport(ModelMap model) {
        model.addAttribute("books", reportService.getMostBorrowedBooks(100));
        model.addAttribute("genreStats", reportService.getBooksByGenreStatistics());
        return "reports/books";
    }
    
    @GetMapping("/inventory")
    public String inventoryReport(ModelMap model) {
        model.addAttribute("inventory", reportService.getInventoryStatus());
        model.addAttribute("lowStock", reportService.getLowStockBooks(5));
        model.addAttribute("overdueBooks", reportService.getOverdueBooks());
        return "reports/inventory";
    }
    
    @GetMapping("/notifications")
    public String notificationsReport(ModelMap model) {
        model.addAttribute("dueSoon", reportService.checkDueDateAlerts(3));
        model.addAttribute("overdue", reportService.checkOverdueNotifications());
        model.addAttribute("newArrivals", reportService.getNewArrivalsThisWeek());
        return "reports/notifications";
    }
    
    @GetMapping("/export/dashboard/pdf")
    public ResponseEntity<byte[]> exportDashboardPDF() {
        try {
            ByteArrayOutputStream pdfStream = reportService.exportDashboardToPDF();
            
            if (pdfStream.size() == 0) {
                // Return a simple PDF placeholder for now
                String content = "Dashboard Report\n\nThis feature is under development.\n\nPlease use Excel export for now.";
                pdfStream.write(content.getBytes());
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "dashboard-report.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/export/inventory/excel")
    public ResponseEntity<byte[]> exportInventoryExcel() {
        try {
            ByteArrayOutputStream excelStream = reportService.exportInventoryToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "inventory-report.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/export/members/excel")
    public ResponseEntity<byte[]> exportMembersExcel() {
        try {
            ByteArrayOutputStream excelStream = reportService.exportMemberReportToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "member-statistics.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/export/books/excel")
    public ResponseEntity<byte[]> exportBooksExcel() {
        try {
            ByteArrayOutputStream excelStream = reportService.exportBookStatisticsToExcel();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "book-statistics.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

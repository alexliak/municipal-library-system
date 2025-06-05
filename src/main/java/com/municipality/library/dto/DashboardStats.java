package com.municipality.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalBooks;
    private long totalMembers;
    private long activeLoans;
    private long overdueLoans;
    private long totalAuthors;
    private long newBooksThisMonth;
    private long newMembersThisMonth;
    private double averageLoansPerMember;
    
    // Today's activity fields
    private long booksAddedToday;
    private long loansToday;
    private long returnsToday;
}
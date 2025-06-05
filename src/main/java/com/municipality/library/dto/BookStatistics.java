package com.municipality.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookStatistics {
    private String isbn;
    private String title;
    private String authorsString;
    private String genre;
    private Integer totalCopies;
    private Integer availableCopies;
    private Long loanCount;
    private Double averageRating;
    private Integer totalReviews;
    private LocalDate publicationDate;
    private LocalDateTime createdAt;
    
    // For overdue books
    private String borrowerName;
    private LocalDateTime dueDate;
    private Long daysOverdue;
    
    // Constructor for basic book stats
    public BookStatistics(String isbn, String title, Integer totalCopies, 
                         Integer availableCopies, Long loanCount) {
        this.isbn = isbn;
        this.title = title;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.loanCount = loanCount;
    }
}
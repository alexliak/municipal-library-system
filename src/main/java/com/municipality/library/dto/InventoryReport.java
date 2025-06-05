package com.municipality.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReport {
    private long totalBooks;
    private long availableBooks;
    private long borrowedBooks;
    private long uniqueTitles;
    
    public InventoryReport(long totalBooks, long availableBooks, long uniqueTitles) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.borrowedBooks = totalBooks - availableBooks;
        this.uniqueTitles = uniqueTitles;
    }
}
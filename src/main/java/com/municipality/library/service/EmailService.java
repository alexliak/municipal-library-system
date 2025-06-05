package com.municipality.library.service;

import com.municipality.library.entity.User;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.Loan;
import java.util.List;

public interface EmailService {
    
    // Send due date reminder
    void sendDueDateReminder(User user, Loan loan);
    
    // Send overdue notification
    void sendOverdueNotification(User user, Loan loan);
    
    // Send new arrivals notification
    void sendNewArrivalsNotification(User user, List<Book> newBooks);
    
    // Send loan confirmation
    void sendLoanConfirmation(User user, Loan loan);
    
    // Send return confirmation
    void sendReturnConfirmation(User user, Loan loan);
    
    // Send welcome email
    void sendWelcomeEmail(User user);
    
    // Send batch notifications (scheduled)
    void sendDailyNotifications();
    void sendWeeklyNewArrivals();
}

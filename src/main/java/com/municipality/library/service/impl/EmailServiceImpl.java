package com.municipality.library.service.impl;

import com.municipality.library.entity.User;
import com.municipality.library.entity.Book;
import com.municipality.library.entity.Loan;
import com.municipality.library.entity.LoanStatus;
import com.municipality.library.repository.LoanRepository;
import com.municipality.library.repository.UserRepository;
import com.municipality.library.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired(required = false) // Make it optional if mail is not configured
    private JavaMailSender mailSender;
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Value("${spring.mail.username:library@municipality.com}")
    private String fromEmail;
    
    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void sendDueDateReminder(User user, Loan loan) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): Due date reminder for " + user.getEmail());
            return;
        }
        
        String subject = "Library Book Due Date Reminder";
        String text = String.format(
            "Dear %s,\n\n" +
            "This is a reminder that the following book is due for return:\n\n" +
            "Title: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book by the due date to avoid late fees.\n\n" +
            "Thank you,\n" +
            "Municipal Library",
            user.getFullName(),
            loan.getBook().getTitle(),
            loan.getDueDate().format(dateFormatter)
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendOverdueNotification(User user, Loan loan) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): Overdue notification for " + user.getEmail());
            return;
        }
        
        long daysOverdue = java.time.Duration.between(loan.getDueDate(), LocalDateTime.now()).toDays();
        
        String subject = "Library Book Overdue Notice";
        String text = String.format(
            "Dear %s,\n\n" +
            "The following book is %d days overdue:\n\n" +
            "Title: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book immediately to avoid additional late fees.\n\n" +
            "Thank you,\n" +
            "Municipal Library",
            user.getFullName(),
            daysOverdue,
            loan.getBook().getTitle(),
            loan.getDueDate().format(dateFormatter)
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendNewArrivalsNotification(User user, List<Book> newBooks) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): New arrivals for " + user.getEmail());
            return;
        }
        
        String bookList = newBooks.stream()
            .map(book -> String.format("- %s by %s", 
                book.getTitle(), 
                book.getAuthors().stream()
                    .map(a -> a.getName())
                    .collect(Collectors.joining(", "))))
            .collect(Collectors.joining("\n"));
        
        String subject = "New Books at Municipal Library";
        String text = String.format(
            "Dear %s,\n\n" +
            "We're excited to share our new arrivals this week:\n\n" +
            "%s\n\n" +
            "Visit the library to check out these new titles!\n\n" +
            "Happy reading,\n" +
            "Municipal Library",
            user.getFullName(),
            bookList
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendLoanConfirmation(User user, Loan loan) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): Loan confirmation for " + user.getEmail());
            return;
        }
        
        String subject = "Book Loan Confirmation";
        String text = String.format(
            "Dear %s,\n\n" +
            "You have successfully borrowed the following book:\n\n" +
            "Title: %s\n" +
            "Loan Date: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book by the due date.\n\n" +
            "Thank you for using Municipal Library!\n" +
            "Municipal Library",
            user.getFullName(),
            loan.getBook().getTitle(),
            loan.getLoanDate().format(dateFormatter),
            loan.getDueDate().format(dateFormatter)
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendReturnConfirmation(User user, Loan loan) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): Return confirmation for " + user.getEmail());
            return;
        }
        
        String subject = "Book Return Confirmation";
        String text = String.format(
            "Dear %s,\n\n" +
            "Thank you for returning the following book:\n\n" +
            "Title: %s\n" +
            "Return Date: %s\n\n" +
            "We hope you enjoyed reading it!\n\n" +
            "Visit us again soon,\n" +
            "Municipal Library",
            user.getFullName(),
            loan.getBook().getTitle(),
            loan.getReturnDate().format(dateFormatter)
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendWelcomeEmail(User user) {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Email notification (disabled): Welcome email for " + user.getEmail());
            return;
        }
        
        String subject = "Welcome to Municipal Library";
        String text = String.format(
            "Dear %s,\n\n" +
            "Welcome to Municipal Library! Your account has been successfully created.\n\n" +
            "Username: %s\n\n" +
            "You can now:\n" +
            "- Browse our book catalog\n" +
            "- Borrow books\n" +
            "- Rate and review books\n" +
            "- Track your reading history\n\n" +
            "Visit our library or log in online to get started!\n\n" +
            "Happy reading,\n" +
            "Municipal Library",
            user.getFullName(),
            user.getUsername()
        );
        
        sendSimpleMessage(user.getEmail(), subject, text);
    }
    
    @Override
    public void sendDailyNotifications() {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Daily email notifications skipped (email disabled)");
            return;
        }
        
        // Send due date reminders (3 days before)
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
        List<Loan> loansDueSoon = loanRepository.findByStatusAndDueDateBetween(
            LoanStatus.ACTIVE, LocalDateTime.now(), threeDaysFromNow);
        
        for (Loan loan : loansDueSoon) {
            sendDueDateReminder(loan.getUser(), loan);
        }
        
        // Send overdue notifications
        List<Loan> overdueLoans = loanRepository.findByStatusAndDueDateBefore(
            LoanStatus.ACTIVE, LocalDateTime.now());
        
        for (Loan loan : overdueLoans) {
            sendOverdueNotification(loan.getUser(), loan);
        }
        
        System.out.println("Daily notifications sent: " + 
            loansDueSoon.size() + " due reminders, " + 
            overdueLoans.size() + " overdue notices");
    }
    
    @Override
    public void sendWeeklyNewArrivals() {
        if (!emailEnabled || mailSender == null) {
            System.out.println("Weekly new arrivals notifications skipped (email disabled)");
            return;
        }
        
        // This would typically be scheduled weekly
        // For now, it's a placeholder
        System.out.println("Weekly new arrivals notifications would be sent here");
    }
    
    private void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
    
    private void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            System.out.println("HTML email sent to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
        }
    }
}

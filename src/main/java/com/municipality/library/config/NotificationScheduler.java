package com.municipality.library.config;

import com.municipality.library.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class NotificationScheduler {
    
    @Autowired
    private EmailService emailService;
    
    // Run daily at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyNotifications() {
        System.out.println("Running daily notification job...");
        emailService.sendDailyNotifications();
    }
    
    // Run every Monday at 10 AM
    @Scheduled(cron = "0 0 10 * * MON")
    public void sendWeeklyNewArrivals() {
        System.out.println("Running weekly new arrivals job...");
        emailService.sendWeeklyNewArrivals();
    }
}

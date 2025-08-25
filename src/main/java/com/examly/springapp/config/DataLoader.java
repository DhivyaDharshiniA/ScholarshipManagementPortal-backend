// DataLoader.java
package com.examly.springapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.examly.springapp.service.NotificationService;
import org.springframework.stereotype.Component;
import com.examly.springapp.entity.NotificationType;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void run(String... args) throws Exception {
        // Create sample notifications for testing
        notificationService.createNotificationForStudent(1L, "Welcome to Student Portal",
                "Your account has been successfully created. Complete your profile to get started.",
                NotificationType.SUCCESS);

        notificationService.createNotificationForStudent(1L, "Profile Under Review",
                "Your student profile is currently being reviewed by an administrator.",
                NotificationType.INFO);

        notificationService.createNotificationForStudent(1L, "New Message",
                "You have received a new message from your academic advisor.",
                NotificationType.INFO);
    }
}
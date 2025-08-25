package com.examly.springapp.service;

import com.examly.springapp.entity.Notification;
import com.examly.springapp.entity.NotificationType;
import com.examly.springapp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Page<Notification> getNotificationsByStudentId(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId, pageable);
    }

    public List<Notification> getUnreadNotifications(Long studentId) {
        return notificationRepository.findByStudentIdAndIsReadFalseOrderByCreatedAtDesc(studentId);
    }

    public Long getUnreadCount(Long studentId) {
        return notificationRepository.countByStudentIdAndIsReadFalse(studentId);
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void createNotificationForStudent(Long studentId, String title, String message, NotificationType type) {
        Notification notification = new Notification(studentId, title, message, type);
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId, Long studentId) {
        notificationRepository.markAsRead(notificationId, studentId);
    }

    public void markAllAsRead(Long studentId) {
        notificationRepository.markAllAsRead(studentId);
    }

    public void deleteNotification(Long notificationId, Long studentId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent() && notification.get().getStudentId().equals(studentId)) {
            notificationRepository.deleteById(notificationId);
        }
    }

    public Optional<Notification> getNotification(Long notificationId, Long studentId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent() && notification.get().getStudentId().equals(studentId)) {
            return notification;
        }
        return Optional.empty();
    }
}

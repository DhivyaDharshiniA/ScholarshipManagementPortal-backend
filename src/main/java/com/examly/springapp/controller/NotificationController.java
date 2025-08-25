package com.examly.springapp.controller;

import com.examly.springapp.dto.CreateNotificationRequest;
import com.examly.springapp.dto.NotificationDTO;
import com.examly.springapp.dto.NotificationResponse;
import com.examly.springapp.entity.Notification;
import com.examly.springapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<NotificationResponse> getNotificationsByStudentId(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Notification> notificationsPage = notificationService.getNotificationsByStudentId(studentId, page, size);
        Long unreadCount = notificationService.getUnreadCount(studentId);

        List<NotificationDTO> notificationDTOs = notificationsPage.getContent()
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());

        NotificationResponse response = new NotificationResponse(notificationDTOs, unreadCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long studentId) {
        Long unreadCount = notificationService.getUnreadCount(studentId);
        return ResponseEntity.ok(unreadCount);
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setStudentId(request.getStudentId());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setLink(request.getLink());

        Notification savedNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(new NotificationDTO(savedNotification));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @RequestParam Long studentId) {
        notificationService.markAsRead(id, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/student/{studentId}/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long studentId) {
        notificationService.markAllAsRead(studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, @RequestParam Long studentId) {
        notificationService.deleteNotification(id, studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id, @RequestParam Long studentId) {
        return notificationService.getNotification(id, studentId)
                .map(NotificationDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

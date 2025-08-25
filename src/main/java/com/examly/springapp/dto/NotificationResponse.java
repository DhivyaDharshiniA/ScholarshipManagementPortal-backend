package com.examly.springapp.dto;

import java.util.List;

public class NotificationResponse {
    private List<NotificationDTO> notifications;
    private Long unreadCount;

    public NotificationResponse() {}

    public NotificationResponse(List<NotificationDTO> notifications, Long unreadCount) {
        this.notifications = notifications;
        this.unreadCount = unreadCount;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
}

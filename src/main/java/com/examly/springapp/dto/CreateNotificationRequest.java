package com.examly.springapp.dto;

import com.examly.springapp.entity.NotificationType;

public class CreateNotificationRequest {
    private Long studentId;
    private String title;
    private String message;
    private NotificationType type;
    private String link;

    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}

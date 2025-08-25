package com.examly.springapp.dto;

import com.examly.springapp.entity.Application;
import com.examly.springapp.entity.Document;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDTO {

    private Long id;
    private Long userId;
    private Long scholarshipId;
    private String userName;
    private String scholarshipName;
    private Double amount;
    private String category;
    private LocalDate deadline;
    private String status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    private List<DocumentInfo> documents = new ArrayList<>();

    public ApplicationDTO() {}

    public ApplicationDTO(Application application) {
        this.id = application.getId();
        this.userId = application.getUser().getId();
        this.userName = application.getUser().getUsername();

        if (application.getScholarship() != null) {
            this.scholarshipId = application.getScholarship().getId();
            this.scholarshipName = application.getScholarship().getName() != null
                    ? application.getScholarship().getName()
                    : "Unnamed Scholarship";
            this.amount = application.getScholarship().getAmount();
            this.category = application.getScholarship().getCategory() != null
                    ? application.getScholarship().getCategory()
                    : "Uncategorized";
            this.deadline = application.getScholarship().getDeadline();
        } else {
            this.scholarshipId = null;
            this.scholarshipName = "Unnamed Scholarship";
            this.amount = 0.0;
            this.category = "Uncategorized";
            this.deadline = null;
        }

        this.status = application.getStatus().name();
        this.appliedAt = application.getAppliedAt();
        this.updatedAt = application.getUpdatedAt();

        // Convert documents to DocumentInfo objects
        if (application.getDocuments() != null && !application.getDocuments().isEmpty()) {
            for (Document doc : application.getDocuments()) {
                this.documents.add(new DocumentInfo(
                        doc.getFileName(),
                        "http://localhost:8080/api/applications/documents/" + doc.getFileName(),
                        doc.getFileType(),
                        doc.getSize()
                ));
            }
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(Long scholarshipId) { this.scholarshipId = scholarshipId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getScholarshipName() { return scholarshipName; }
    public void setScholarshipName(String scholarshipName) { this.scholarshipName = scholarshipName; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<DocumentInfo> getDocuments() { return documents; }
    public void setDocuments(List<DocumentInfo> documents) { this.documents = documents; }

    public static class DocumentInfo {
        private String name;
        private String url;
        private String type;
        private long size;

        public DocumentInfo() {}
        public DocumentInfo(String name, String url, String type, long size) {
            this.name = name;
            this.url = url;
            this.type = type;
            this.size = size;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }
}

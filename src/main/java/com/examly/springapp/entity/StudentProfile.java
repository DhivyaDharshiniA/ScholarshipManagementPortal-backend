package com.examly.springapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    private LocalDate dateOfBirth;
    @Column(length = 1000)
    private String address;

    // Academic
    @NotBlank(message = "Major is required")
    private String major;

    @NotBlank(message = "Year is required")
    private String year;

    @DecimalMin(value = "0.0", inclusive = true, message = "GPA must be >= 0.0")
    @DecimalMax(value = "4.0", message = "GPA must not exceed 4.0")
    private Double gpa;

    @NotBlank(message = "University is required")
    private String university;

    private LocalDate expectedGraduation;

    @Column(length = 2000)
    private String bio;

    // Status lifecycle: incomplete -> pending -> approved/rejected
    @Column(nullable = false)
    private String status = "incomplete";

    // Edit request lifecycle on approved profiles
    private boolean editRequested = false;  // student asked to edit
    private boolean editApproved  = false;  // admin allowed editing session
    private String editReason;

    @Column(length = 4000)
    private String requestedChangesJson;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;     // first submit → pending
    private LocalDateTime approvedAt;      // admin approved
    private LocalDateTime editRequestedAt; // student requested edit

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---------- Business rules ----------

    // Can user currently edit the form?
    public boolean canEdit() {
        if ("incomplete".equals(this.status)) return true;
        if ("pending".equals(this.status)) return true;
        if ("approved".equals(this.status)) return this.editApproved;
        if ("rejected".equals(this.status)) return true;
        return false;
    }

    // Can user request editing?
    public boolean canRequestEdit() {
        return "approved".equals(this.status) && !this.editRequested && !this.editApproved;
    }

    // First submission
    public void markAsSubmitted() {
        this.status = "pending";
        this.submittedAt = LocalDateTime.now();
    }

    // Admin decisions for profile lifecycle
    public void approve() {
        this.status = "approved";
        this.approvedAt = LocalDateTime.now();
        // Close any edit session flags
        this.editRequested = false;
        this.editApproved = false;
        this.editReason = null;
        this.requestedChangesJson = null;
    }

    public void reject() {
        this.status = "rejected";
        this.editRequested = false;
        this.editApproved = false;
    }

    // Admin decisions for edit request
    public void approveEditRequest() {
        this.editApproved = true;   // unlock form
        this.editRequested = false; // request is consumed
    }

    public void rejectEditRequest() {
        this.editRequested = false;
        this.editApproved  = false;
        this.editReason = null;
        this.requestedChangesJson = null;
    }
}

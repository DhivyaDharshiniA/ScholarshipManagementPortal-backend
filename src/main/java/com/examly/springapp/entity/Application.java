package com.examly.springapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to user (student)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User user;

    // Link to scholarship
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scholarship_id", nullable = false)
    private Scholarship scholarship;

    @Column(length = 500)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Document> documents;

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED
    }
}
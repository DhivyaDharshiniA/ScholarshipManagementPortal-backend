package com.examly.springapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Scholarship name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    private String category;

    private String eligibilityCriteria;

    @Positive(message = "Amount must be positive")
    private double amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDate deadline;

    private String status = "Active";
}

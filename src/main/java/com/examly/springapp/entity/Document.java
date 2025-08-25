package com.examly.springapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileType;

    private long size;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
}
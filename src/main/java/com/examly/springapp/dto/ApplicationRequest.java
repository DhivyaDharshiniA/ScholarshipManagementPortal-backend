package com.examly.springapp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    private Long studentId;
    private Long scholarshipId;

}
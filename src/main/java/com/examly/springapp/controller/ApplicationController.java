package com.examly.springapp.controller;

import com.examly.springapp.dto.ApplicationDTO;
import com.examly.springapp.entity.Application;
import com.examly.springapp.service.ApplicationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/apply", consumes = {"multipart/form-data"})
    public ResponseEntity<?> apply(
            @RequestParam("studentId") Long studentId,
            @RequestParam("scholarshipId") Long scholarshipId,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        log.info("Received apply request: studentId={}, scholarshipId={}, file={}",
                studentId, scholarshipId, file != null ? file.getOriginalFilename() : "none");

        try {
            Application application = applicationService.apply(studentId, scholarshipId, file);
            return ResponseEntity.ok(new ApplicationDTO(application));
        } catch (RuntimeException e) {
            log.error("Application failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByUser(@PathVariable Long userId) {
        try {
            List<Application> applications = applicationService.getApplicationsByUserId(userId);
            List<ApplicationDTO> dtos = applications.stream()
                    .map(ApplicationDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Error fetching applications for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        try {
            List<Application> applications = applicationService.getAllApplications();
            List<ApplicationDTO> dtos = applications.stream()
                    .map(ApplicationDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Error fetching all applications: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {

        try {
            // Validate status
            List<String> validStatuses = Arrays.asList("PENDING", "APPROVED", "REJECTED");
            if (!validStatuses.contains(request.getStatus().toUpperCase())) {
                return ResponseEntity.badRequest().body("Invalid status. Must be one of: PENDING, APPROVED, REJECTED");
            }

            Application updated = applicationService.updateApplicationStatus(id, request.getStatus().toUpperCase());
            return ResponseEntity.ok(new ApplicationDTO(updated));
        } catch (RuntimeException e) {
            log.error("Failed to update status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error updating status: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    @GetMapping("/documents/{filename:.+}")
    public ResponseEntity<Resource> getDocument(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving document {}: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String filename) {
        if (filename == null) return "application/octet-stream";

        String extension = filename.contains(".")
                ? filename.substring(filename.lastIndexOf(".") + 1).toLowerCase()
                : "";

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    @Data
    public static class StatusUpdateRequest {
        private String status;
    }
}
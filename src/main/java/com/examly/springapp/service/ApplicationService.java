package com.examly.springapp.service;

import com.examly.springapp.entity.Application;
import com.examly.springapp.entity.Document;
import com.examly.springapp.entity.Scholarship;
import com.examly.springapp.entity.User;
import com.examly.springapp.repository.ApplicationRepository;
import com.examly.springapp.repository.DocumentRepository;
import com.examly.springapp.repository.ScholarshipRepository;
import com.examly.springapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public Application apply(Long userId, Long scholarshipId, MultipartFile file) {
        User student = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        // Prevent duplicate applications
        applicationRepository.findByUser_IdAndScholarship_Id(student.getId(), scholarship.getId())
                .ifPresent(a -> { throw new RuntimeException("You have already applied for this scholarship"); });

        // Deadline validation
        if (scholarship.getDeadline() != null && scholarship.getDeadline().isBefore(LocalDate.now())) {
            throw new RuntimeException("The deadline for this scholarship has passed");
        }

        // Create and save application
        Application application = new Application();
        application.setUser(student);
        application.setScholarship(scholarship);
        application.setStatus(Application.Status.PENDING);

        Application savedApplication = applicationRepository.save(application);

        // Save uploaded file if provided
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                // Create and save document entity
                Document document = new Document();
                document.setFileName(fileName);
                document.setFileType(file.getContentType());
                document.setSize(file.getSize());
                document.setApplication(savedApplication);

                documentRepository.save(document);

            } catch (IOException e) {
                throw new RuntimeException("File upload failed: " + e.getMessage());
            }
        }

        return savedApplication;
    }

    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUser_Id(userId);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application updateApplicationStatus(Long id, String statusStr) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        try {
            // Convert String to enum
            Application.Status status = Application.Status.valueOf(statusStr.toUpperCase());
            app.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + statusStr);
        }

        return applicationRepository.save(app);
    }

}

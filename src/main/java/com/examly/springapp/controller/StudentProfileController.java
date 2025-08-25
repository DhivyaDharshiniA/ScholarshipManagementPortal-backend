package com.examly.springapp.controller;

import com.examly.springapp.entity.StudentProfile;
import com.examly.springapp.repository.StudentProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class StudentProfileController {

    @Autowired
    private StudentProfileRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // -------- Create profile (new users) --------
    @PostMapping
    public ResponseEntity<?> createProfile(@Valid @RequestBody StudentProfile profile) {
        try {
            if (repository.existsByEmail(profile.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");
            }
            profile.setStatus("incomplete");
            profile.setEditRequested(false);
            profile.setEditApproved(false);

            StudentProfile saved = repository.save(profile);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating profile: " + e.getMessage());
        }
    }

    // -------- Update profile (respecting lock rules) --------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @Valid @RequestBody StudentProfile incoming) {
        try {
            Optional<StudentProfile> optional = repository.findById(id);
            if (optional.isEmpty()) return ResponseEntity.notFound().build();

            StudentProfile existing = optional.get();

            if (!existing.canEdit()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Profile cannot be edited. Status=" + existing.getStatus() +
                                ", editApproved=" + existing.isEditApproved());
            }

            // Apply changes
            copyEditableFields(existing, incoming);

            // First-time submission: incomplete -> pending
            if ("incomplete".equals(existing.getStatus())) {
                existing.markAsSubmitted();
            }

            // If it was an approved edit session, close the session after save (lock again)
            if ("approved".equals(existing.getStatus()) && existing.isEditApproved()) {
                existing.setEditApproved(false); // lock after this save
                existing.setEditRequested(false);
                existing.setRequestedChangesJson(null);
                existing.setEditReason(null);
            }

            StudentProfile saved = repository.save(existing);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating profile: " + e.getMessage());
        }
    }

    private void copyEditableFields(StudentProfile existing, StudentProfile in) {
        existing.setFirstName(in.getFirstName());
        existing.setLastName(in.getLastName());
        existing.setEmail(in.getEmail());
        existing.setPhone(in.getPhone());
        existing.setDateOfBirth(in.getDateOfBirth());
        existing.setAddress(in.getAddress());
        existing.setMajor(in.getMajor());
        existing.setYear(in.getYear());
        existing.setGpa(in.getGpa());
        existing.setUniversity(in.getUniversity());
        existing.setExpectedGraduation(in.getExpectedGraduation());
        existing.setBio(in.getBio());
    }

    // -------- Reads --------
    @GetMapping
    public ResponseEntity<List<StudentProfile>> getAllProfiles() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentProfile> getById(@PathVariable Long id) {
        return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // If you use "studentId" as the profile ID from auth, this simply fetches by id
    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentProfile> getByStudentId(@PathVariable Long studentId) {
        return repository.findById(studentId).map(ResponseEntity::ok).orElse(ResponseEntity.ok(null));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StudentProfile> getByEmail(@PathVariable String email) {
        return repository.findByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // -------- Delete --------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repository.findById(id).map(p -> {
            repository.delete(p);
            return ResponseEntity.ok("Deleted");
        }).orElse(ResponseEntity.notFound().build());
    }

    // -------- Student: can-edit flag --------
    @GetMapping("/{id}/can-edit")
    public ResponseEntity<Map<String, Object>> canEdit(@PathVariable Long id) {
        Optional<StudentProfile> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of("profileExists", false, "canEdit", false));
        }
        StudentProfile p = opt.get();
        return ResponseEntity.ok(Map.of(
                "profileExists", true,
                "canEdit", p.canEdit(),
                "status", p.getStatus(),
                "editRequested", p.isEditRequested(),
                "editApproved", p.isEditApproved()
        ));
    }

    // -------- Student: submit edit request --------
    @PostMapping("/edit-request")
    public ResponseEntity<?> submitEditRequest(@RequestBody Map<String, Object> payload) {
        try {
            Long studentId = Long.valueOf(payload.get("studentId").toString());
            String message = (String) payload.getOrDefault("message", "");
            @SuppressWarnings("unchecked")
            Map<String, Object> requestedChanges = (Map<String, Object>) payload.get("requestedChanges");

            Optional<StudentProfile> opt = repository.findById(studentId);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            StudentProfile p = opt.get();

            if (!p.canRequestEdit()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Cannot request edit. Status=" + p.getStatus() +
                                ", editRequested=" + p.isEditRequested() + ", editApproved=" + p.isEditApproved());
            }

            p.setEditRequested(true);
            p.setEditReason(message);
            p.setEditRequestedAt(LocalDateTime.now());

            if (requestedChanges != null) {
                try {
                    String json = objectMapper.writeValueAsString(requestedChanges);
                    p.setRequestedChangesJson(json);
                } catch (Exception e) {
                    // Fallback to plain string – admin UI will guard JSON parse
                    p.setRequestedChangesJson(String.valueOf(requestedChanges));
                }
            }

            repository.save(p);
            return ResponseEntity.ok("Edit request submitted");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting edit request: " + e.getMessage());
        }
    }

    // -------- Admin: list pending edit requests --------
    @GetMapping("/pending-edits")
    public ResponseEntity<List<StudentProfile>> getPendingEditRequests() {
        // OPTION A (strict): only show requests from approved profiles (recommended with your rules)
        return ResponseEntity.ok(repository.findByEditRequestedTrueAndStatus("approved"));

        // OPTION B (lenient): show any editRequested=true
        // return ResponseEntity.ok(repository.findByEditRequestedTrue());
    }

    // -------- Admin: approve/reject NEW profiles --------
    @PostMapping("/profiles/{id}/{decision}")
    public ResponseEntity<?> handleProfileApproval(@PathVariable Long id, @PathVariable String decision) {
        try {
            Optional<StudentProfile> opt = repository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            StudentProfile p = opt.get();
            if (!"pending".equals(p.getStatus())) {
                return ResponseEntity.badRequest()
                        .body("Profile is not pending. Current status=" + p.getStatus());
            }

            if ("approve".equalsIgnoreCase(decision)) {
                p.approve();
            } else if ("reject".equalsIgnoreCase(decision)) {
                p.reject();
            } else {
                return ResponseEntity.badRequest().body("Invalid decision. Use approve/reject");
            }

            repository.save(p);
            return ResponseEntity.ok("Profile " + decision + "d");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing profile approval: " + e.getMessage());
        }
    }

    // -------- Admin: approve/reject EDIT requests --------
    @PostMapping("/edit-requests/{id}/{decision}")
    public ResponseEntity<?> handleEditRequest(@PathVariable Long id, @PathVariable String decision) {
        try {
            Optional<StudentProfile> opt = repository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            StudentProfile p = opt.get();

            if (!p.isEditRequested()) {
                return ResponseEntity.badRequest().body("No pending edit request for this profile");
            }

            if ("approve".equalsIgnoreCase(decision)) {
                p.approveEditRequest(); // unlocks the profile for editing
            } else if ("reject".equalsIgnoreCase(decision)) {
                p.rejectEditRequest();
            } else {
                return ResponseEntity.badRequest().body("Invalid decision. Use approve/reject");
            }

            repository.save(p);
            return ResponseEntity.ok("Edit request " + decision + "d");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing edit request: " + e.getMessage());
        }
    }
}

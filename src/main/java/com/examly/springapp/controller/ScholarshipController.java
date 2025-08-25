package com.examly.springapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.examly.springapp.entity.Scholarship;
import com.examly.springapp.service.ScholarshipService;

@RestController
@RequestMapping("/api/scholarships")
@CrossOrigin(origins = "http://localhost:3000")
public class ScholarshipController {

    @Autowired
    private ScholarshipService scholarshipService;

    @GetMapping
    public List<Scholarship> getAllScholarships() {
        return scholarshipService.getAllScholarships();
    }

    @PostMapping("/admin")
    public Scholarship createScholarship(@RequestBody Scholarship scholarship) {
        return scholarshipService.createScholarship(scholarship);
    }
//
//    @DeleteMapping("/admin/{id}")
//    public void deleteScholarship(@PathVariable Long id) {
//        scholarshipService.deleteScholarship(id);
//    }


    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteScholarship(@PathVariable Long id) {
        try {
            boolean deleted = scholarshipService.deleteScholarship(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scholarship not found");
            }
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting scholarship: " + e.getMessage());
        }
    }

    @PutMapping("/admin/{id}")
    public Scholarship updateScholarship(@PathVariable Long id, @RequestBody Scholarship scholarship) {
        return scholarshipService.updateScholarship(id, scholarship);
    }

}

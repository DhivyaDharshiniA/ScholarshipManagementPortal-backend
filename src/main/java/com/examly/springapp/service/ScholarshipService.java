package com.examly.springapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.examly.springapp.entity.Scholarship;
import com.examly.springapp.repository.ScholarshipRepository;

@Service
public class ScholarshipService {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    public List<Scholarship> getAllScholarships() {
        return scholarshipRepository.findAll();
    }

    public Scholarship getScholarshipById(Long id) {
        return scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));
    }

    public Scholarship createScholarship(Scholarship scholarship) {
        return scholarshipRepository.save(scholarship);
    }

//    public void deleteScholarship(Long id) {
//        scholarshipRepository.deleteById(id);
//    }


    public Scholarship updateScholarship(Long id, Scholarship updatedScholarship) {
        Scholarship existing = scholarshipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scholarship not found"));

        existing.setName(updatedScholarship.getName());
        existing.setDescription(updatedScholarship.getDescription());
        existing.setCategory(updatedScholarship.getCategory());
        existing.setEligibilityCriteria(updatedScholarship.getEligibilityCriteria());
        existing.setAmount(updatedScholarship.getAmount());
        existing.setDeadline(updatedScholarship.getDeadline());
        existing.setStatus(updatedScholarship.getStatus());

        return scholarshipRepository.save(existing);
    }

    public boolean deleteScholarship(Long id) {
        if (!scholarshipRepository.existsById(id)) {
            return false;
        }
        scholarshipRepository.deleteById(id);
        return true;
    }
}

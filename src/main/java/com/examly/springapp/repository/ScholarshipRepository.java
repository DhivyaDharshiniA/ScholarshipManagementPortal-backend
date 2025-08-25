package com.examly.springapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examly.springapp.entity.Scholarship;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {}

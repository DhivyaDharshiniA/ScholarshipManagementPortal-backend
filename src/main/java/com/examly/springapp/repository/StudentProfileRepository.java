package com.examly.springapp.repository;

import com.examly.springapp.entity.StudentProfile;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    boolean existsByEmail(String email);

    Optional<StudentProfile> findByEmail(String email);

    List<StudentProfile> findByStatus(String status);

    @Query("SELECT p FROM StudentProfile p WHERE p.editRequested = true AND p.status = :status")
    List<StudentProfile> findByEditRequestedTrueAndStatus(@Param("status") String status);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM StudentProfile p " +
            "WHERE p.id = :id AND p.editRequested = true AND p.status = :status")
    boolean existsByIdAndEditRequestedTrueAndStatus(@Param("id") Long id, @Param("status") String status);

    @Query("SELECT p FROM StudentProfile p WHERE p.status = 'pending' OR (p.status = 'approved' AND (p.editRequested = true OR p.editApproved = true))")
    List<StudentProfile> findProfilesNeedingAdminAttention();
}

package com.examly.springapp.repository;

import com.examly.springapp.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUser_IdAndScholarship_Id(Long userId, Long scholarshipId);

    @Query("SELECT a FROM Application a JOIN FETCH a.scholarship WHERE a.user.id = :userId")
    List<Application> findByUserIdWithScholarship(@Param("userId") Long userId);

    List<Application> findByUser_Id(Long userId);
}

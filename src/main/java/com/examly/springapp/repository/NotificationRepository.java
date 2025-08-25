package com.examly.springapp.repository;

import com.examly.springapp.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    List<Notification> findByStudentIdAndIsReadFalseOrderByCreatedAtDesc(Long studentId);

    Long countByStudentIdAndIsReadFalse(Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.studentId = :studentId AND n.isRead = false")
    int markAllAsRead(@Param("studentId") Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.studentId = :studentId")
    int markAsRead(@Param("id") Long id, @Param("studentId") Long studentId);
}

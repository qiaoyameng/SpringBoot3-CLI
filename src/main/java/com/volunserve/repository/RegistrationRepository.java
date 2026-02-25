package com.volunserve.repository;

import com.volunserve.entity.Registration;
import com.volunserve.entity.enums.CheckInStatus;
import com.volunserve.entity.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByActivityIdAndVolunteerId(Long activityId, Long volunteerId);

    Page<Registration> findByVolunteerId(Long volunteerId, Pageable pageable);

    Page<Registration> findByActivityId(Long activityId, Pageable pageable);

    List<Registration> findByActivityIdAndStatus(Long activityId, RegistrationStatus status);

    @Query("SELECT r FROM Registration r WHERE r.activity.id = :activityId AND r.status = 'APPROVED' AND r.checkInStatus = :checkInStatus")
    List<Registration> findByActivityIdAndCheckInStatus(@Param("activityId") Long activityId, @Param("checkInStatus") CheckInStatus checkInStatus);

    boolean existsByActivityIdAndVolunteerId(Long activityId, Long volunteerId);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.activity.id = :activityId AND r.status = 'APPROVED'")
    long countApprovedByActivityId(@Param("activityId") Long activityId);

    @Modifying
    @Query("UPDATE Registration r SET r.status = :status, r.reviewedBy.id = :reviewerId, r.reviewedAt = :reviewedAt WHERE r.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") RegistrationStatus status, 
                      @Param("reviewerId") Long reviewerId, @Param("reviewedAt") LocalDateTime reviewedAt);

    @Query("SELECT r FROM Registration r WHERE r.reminderSent = false AND r.status = 'APPROVED' AND r.activity.startTime BETWEEN :now AND :oneHourLater")
    List<Registration> findPendingReminders(@Param("now") LocalDateTime now, @Param("oneHourLater") LocalDateTime oneHourLater);

    @Modifying
    @Query("UPDATE Registration r SET r.reminderSent = true WHERE r.id = :id")
    void markReminderSent(@Param("id") Long id);
}

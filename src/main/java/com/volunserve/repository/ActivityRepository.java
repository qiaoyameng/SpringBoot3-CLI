package com.volunserve.repository;

import com.volunserve.entity.Activity;
import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    Page<Activity> findByStatus(ActivityStatus status, Pageable pageable);

    Page<Activity> findByCategory(ActivityCategory category, Pageable pageable);

    Page<Activity> findByOrganizerId(Long organizerId, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE " +
           "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.description LIKE %:keyword%) AND " +
           "(:category IS NULL OR a.category = :category) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:startTimeFrom IS NULL OR a.startTime >= :startTimeFrom) AND " +
           "(:startTimeTo IS NULL OR a.startTime <= :startTimeTo) AND " +
           "(:organizerId IS NULL OR a.organizer.id = :organizerId)")
    Page<Activity> searchActivities(
            @Param("keyword") String keyword,
            @Param("category") ActivityCategory category,
            @Param("status") ActivityStatus status,
            @Param("startTimeFrom") LocalDateTime startTimeFrom,
            @Param("startTimeTo") LocalDateTime startTimeTo,
            @Param("organizerId") Long organizerId,
            Pageable pageable);

    @Query(value = "SELECT a.* FROM activities a WHERE " +
           "a.status = 'RECRUITING' AND " +
           "(6371 * acos(cos(radians(:lat)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(a.latitude)))) <= :radius",
           nativeQuery = true)
    List<Activity> findNearbyActivities(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radius") Double radiusKm);

    @Modifying
    @Query("UPDATE Activity a SET a.registeredCount = a.registeredCount + 1 WHERE a.id = :id")
    void incrementRegisteredCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Activity a SET a.registeredCount = a.registeredCount - 1 WHERE a.id = :id")
    void decrementRegisteredCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Activity a SET a.approvedCount = a.approvedCount + 1 WHERE a.id = :id")
    void incrementApprovedCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Activity a SET a.approvedCount = a.approvedCount - 1 WHERE a.id = :id")
    void decrementApprovedCount(@Param("id") Long id);

    @Query("SELECT a FROM Activity a WHERE a.status = 'RECRUITING' AND a.startTime <= :time")
    List<Activity> findActivitiesStartingSoon(@Param("time") LocalDateTime time);

    @Query("SELECT a FROM Activity a WHERE a.status = 'IN_PROGRESS' AND a.endTime <= :time")
    List<Activity> findActivitiesEndingSoon(@Param("time") LocalDateTime time);
}

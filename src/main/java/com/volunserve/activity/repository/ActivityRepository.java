package com.volunserve.activity.repository;

import com.volunserve.activity.entity.Activity;
import com.volunserve.activity.enumeration.ActivityCategory;
import com.volunserve.activity.enumeration.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    Page<Activity> findByCategory(ActivityCategory category, Pageable pageable);

    Page<Activity> findByStatus(ActivityStatus status, Pageable pageable);

    Page<Activity> findByCategoryAndStatus(ActivityCategory category, ActivityStatus status, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.status = :status AND a.startTime BETWEEN :from AND :to")
    List<Activity> findByStatusAndStartTimeBetween(
            @Param("status") ActivityStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT a FROM Activity a WHERE a.startTime BETWEEN :from AND :to AND a.status = 'RECRUITING'")
    List<Activity> findUpcomingActivities(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("SELECT a FROM Activity a WHERE a.title LIKE %:keyword% OR a.description LIKE %:keyword% OR a.location LIKE %:keyword%")
    Page<Activity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Activity> findByStatusAndStartTimeBefore(ActivityStatus status, LocalDateTime time);

    List<Activity> findByStatusAndEndTimeBefore(ActivityStatus status, LocalDateTime time);
}

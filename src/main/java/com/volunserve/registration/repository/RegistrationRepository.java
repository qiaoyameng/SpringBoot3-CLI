package com.volunserve.registration.repository;

import com.volunserve.activity.entity.Activity;
import com.volunserve.activity.enumeration.ActivityStatus;
import com.volunserve.registration.entity.Registration;
import com.volunserve.registration.enumeration.RegistrationStatus;
import com.volunserve.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long>, JpaSpecificationExecutor<Registration> {

    Optional<Registration> findByActivityAndUser(Activity activity, User user);

    boolean existsByActivityAndUser(Activity activity, User user);

    List<Registration> findByActivityAndStatus(Activity activity, RegistrationStatus status);

    List<Registration> findByUserAndStatus(User user, RegistrationStatus status);

    Page<Registration> findByActivity(Activity activity, Pageable pageable);

    Page<Registration> findByUser(User user, Pageable pageable);

    @Query("SELECT r FROM Registration r WHERE r.status = 'APPROVED' AND r.activity.status = 'RECRUITING' AND r.activity.startTime BETWEEN :from AND :to AND r.reminded = false")
    List<Registration> findUpcomingReminders(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.activity = :activity AND r.status IN ('PENDING', 'APPROVED', 'CHECKED_IN', 'CHECKED_OUT')")
    int countByActivity(@Param("activity") Activity activity);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.activity = :activity AND r.status = 'APPROVED'")
    int countApprovedByActivity(@Param("activity") Activity activity);

    @Query("SELECT r FROM Registration r WHERE r.status = 'CHECKED_IN' AND r.activity.endTime < :now")
    List<Registration> findCheckedInButNotCheckedOut(@Param("now") LocalDateTime now);

    List<Registration> findByActivityAndStatusIn(Activity activity, List<RegistrationStatus> statuses);
}

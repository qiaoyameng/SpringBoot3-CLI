package com.volunserve.servicerecord.repository;

import com.volunserve.activity.entity.Activity;
import com.volunserve.servicerecord.entity.ServiceRecord;
import com.volunserve.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long>, JpaSpecificationExecutor<ServiceRecord> {

    Optional<ServiceRecord> findByActivityAndUser(Activity activity, User user);

    List<ServiceRecord> findByUser(User user);

    List<ServiceRecord> findByActivity(Activity activity);

    Page<ServiceRecord> findByUser(User user, Pageable pageable);

    Page<ServiceRecord> findByActivity(Activity activity, Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.serviceHours), 0) FROM ServiceRecord s WHERE s.user = :user")
    Double sumServiceHoursByUser(@Param("user") User user);

    @Query("SELECT COUNT(s) FROM ServiceRecord s WHERE s.user = :user")
    int countByUser(@Param("user") User user);

    @Query("SELECT COALESCE(AVG(s.rating), 0) FROM ServiceRecord s WHERE s.user = :user AND s.rating IS NOT NULL")
    Double getAverageRatingByUser(@Param("user") User user);

    List<ServiceRecord> findByUserAndCertificateGenerated(User user, boolean generated);
}

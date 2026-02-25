package com.volunserve.repository;

import com.volunserve.entity.ServiceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    Optional<ServiceRecord> findByRegistrationId(Long registrationId);

    Page<ServiceRecord> findByVolunteerId(Long volunteerId, Pageable pageable);

    List<ServiceRecord> findByActivityId(Long activityId);

    @Query("SELECT SUM(sr.serviceHours) FROM ServiceRecord sr WHERE sr.volunteer.id = :volunteerId")
    Integer sumServiceHoursByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT COUNT(sr) FROM ServiceRecord sr WHERE sr.volunteer.id = :volunteerId")
    long countByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.volunteer.id = :volunteerId AND sr.certificateUrl IS NOT NULL")
    List<ServiceRecord> findWithCertificateByVolunteerId(@Param("volunteerId") Long volunteerId);

    boolean existsByRegistrationId(Long registrationId);
}

package com.volunserve.activity.entity;

import com.volunserve.activity.enumeration.ActivityCategory;
import com.volunserve.activity.enumeration.ActivityStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(nullable = false)
    private Integer requiredVolunteers;

    @Column(nullable = false)
    private Integer currentVolunteers = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityStatus status;

    @Column(length = 500)
    private String requirements;

    @Column(length = 255)
    private String contactPerson;

    @Column(length = 20)
    private String contactPhone;

    @Column(length = 255)
    private String coverImage;

    @Column
    private LocalDateTime checkInStartTime;

    @Column
    private LocalDateTime checkOutEndTime;

    @Column(nullable = false)
    private Boolean autoApprove = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ActivityStatus.RECRUITING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

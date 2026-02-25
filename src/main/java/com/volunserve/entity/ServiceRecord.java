package com.volunserve.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private User volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private Integer serviceHours;

    @Column(nullable = false)
    private LocalDateTime serviceDate;

    @Column(columnDefinition = "TEXT")
    private String volunteerFeedback;

    @Column
    private Integer volunteerRating;

    @Column(columnDefinition = "TEXT")
    private String organizerFeedback;

    @Column
    private Integer organizerRating;

    @Column(length = 200)
    private String certificateUrl;

    @Column
    private LocalDateTime certificateGeneratedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean certificateDownloaded = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

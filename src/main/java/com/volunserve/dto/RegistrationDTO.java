package com.volunserve.dto;

import com.volunserve.entity.enums.CheckInStatus;
import com.volunserve.entity.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long volunteerId;
    private String volunteerName;
    private String volunteerAvatar;
    private RegistrationStatus status;
    private String statusLabel;
    private CheckInStatus checkInStatus;
    private String checkInStatusLabel;
    private String message;
    private String adminComment;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Integer serviceHours;
    private Boolean reminderSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long reviewedBy;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
}

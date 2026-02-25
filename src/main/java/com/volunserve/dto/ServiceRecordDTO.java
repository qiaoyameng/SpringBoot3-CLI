package com.volunserve.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecordDTO {
    private Long id;
    private Long registrationId;
    private Long volunteerId;
    private String volunteerName;
    private String volunteerAvatar;
    private Long activityId;
    private String activityTitle;
    private Integer serviceHours;
    private LocalDateTime serviceDate;
    private String volunteerFeedback;
    private Integer volunteerRating;
    private String organizerFeedback;
    private Integer organizerRating;
    private String certificateUrl;
    private LocalDateTime certificateGeneratedAt;
    private Boolean certificateDownloaded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

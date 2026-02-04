package com.volunserve.activity.dto;

import com.volunserve.activity.enumeration.ActivityCategory;
import com.volunserve.activity.enumeration.ActivityStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer requiredVolunteers;
    private Integer currentVolunteers;
    private ActivityCategory category;
    private ActivityStatus status;
    private String requirements;
    private String contactPerson;
    private String contactPhone;
    private String coverImage;
    private LocalDateTime checkInStartTime;
    private LocalDateTime checkOutEndTime;
    private Boolean autoApprove;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

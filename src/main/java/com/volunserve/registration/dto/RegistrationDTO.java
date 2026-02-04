package com.volunserve.registration.dto;

import com.volunserve.registration.enumeration.RegistrationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistrationDTO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long userId;
    private String userName;
    private RegistrationStatus status;
    private String applyReason;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String rejectReason;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

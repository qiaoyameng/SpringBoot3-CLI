package com.volunserve.servicerecord.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceRecordDTO {
    private Long id;
    private Long activityId;
    private String activityTitle;
    private Long userId;
    private String userName;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private BigDecimal serviceHours;
    private BigDecimal rating;
    private String reviewContent;
    private String feedback;
    private String certificateNumber;
    private LocalDateTime certificateGeneratedAt;
    private Boolean certificateGenerated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

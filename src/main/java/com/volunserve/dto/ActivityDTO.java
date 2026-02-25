package com.volunserve.dto;

import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import com.volunserve.entity.enums.AuditType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Long id;
    private String title;
    private String description;
    private ActivityCategory category;
    private String categoryLabel;
    private ActivityStatus status;
    private String statusLabel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Double latitude;
    private Double longitude;
    private Integer requiredVolunteers;
    private Integer registeredCount;
    private Integer approvedCount;
    private AuditType auditType;
    private String auditTypeLabel;
    private String requirements;
    private String contactInfo;
    private Long organizerId;
    private String organizerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}

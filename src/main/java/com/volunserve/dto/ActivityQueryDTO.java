package com.volunserve.dto;

import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityQueryDTO {
    private String keyword;
    private ActivityCategory category;
    private ActivityStatus status;
    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;
    private Long organizerId;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private String sortBy;
    private String sortOrder;
}

package com.volunserve.servicerecord.dto;

import lombok.Data;

@Data
public class ServiceRecordQueryDTO {
    private Long activityId;
    private Long userId;
    private Boolean certificateGenerated;
    private Integer page = 0;
    private Integer size = 10;
}

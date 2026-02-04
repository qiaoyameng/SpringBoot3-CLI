package com.volunserve.registration.dto;

import com.volunserve.registration.enumeration.RegistrationStatus;
import lombok.Data;

@Data
public class RegistrationQueryDTO {
    private Long activityId;
    private Long userId;
    private RegistrationStatus status;
    private Integer page = 0;
    private Integer size = 10;
}

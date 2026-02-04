package com.volunserve.activity.dto;

import com.volunserve.activity.enumeration.ActivityCategory;
import com.volunserve.activity.enumeration.ActivityStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class ActivityQueryDTO {
    private String keyword;
    private ActivityCategory category;
    private ActivityStatus status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeTo;

    private String location;
    private Integer page = 0;
    private Integer size = 10;
}

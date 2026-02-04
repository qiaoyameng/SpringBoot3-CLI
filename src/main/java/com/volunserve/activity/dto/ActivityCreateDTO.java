package com.volunserve.activity.dto;

import com.volunserve.activity.enumeration.ActivityCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityCreateDTO {
    @NotBlank(message = "活动标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须在未来")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotBlank(message = "活动地点不能为空")
    private String location;

    @NotNull(message = "需求人数不能为空")
    @Min(value = 1, message = "需求人数至少为1")
    private Integer requiredVolunteers;

    @NotNull(message = "活动分类不能为空")
    private ActivityCategory category;

    private String requirements;

    private String contactPerson;

    private String contactPhone;

    private String coverImage;

    private LocalDateTime checkInStartTime;

    private LocalDateTime checkOutEndTime;

    private Boolean autoApprove = false;
}

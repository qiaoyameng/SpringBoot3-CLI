package com.volunserve.dto;

import com.volunserve.entity.enums.ActivityCategory;
import com.volunserve.entity.enums.ActivityStatus;
import com.volunserve.entity.enums.AuditType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpdateDTO {

    @Size(max = 200, message = "活动标题不能超过200字符")
    private String title;

    @Size(max = 5000, message = "活动描述不能超过5000字符")
    private String description;

    private ActivityCategory category;

    @Future(message = "活动开始时间必须是未来时间")
    private LocalDateTime startTime;

    @Future(message = "活动结束时间必须是未来时间")
    private LocalDateTime endTime;

    @Size(max = 300, message = "活动地点不能超过300字符")
    private String location;

    private Double latitude;

    private Double longitude;

    @Min(value = 1, message = "需求人数至少为1")
    @Max(value = 1000, message = "需求人数不能超过1000")
    private Integer requiredVolunteers;

    private ActivityStatus status;

    private AuditType auditType;

    @Size(max = 500, message = "报名要求不能超过500字符")
    private String requirements;

    @Size(max = 500, message = "联系方式不能超过500字符")
    private String contactInfo;
}

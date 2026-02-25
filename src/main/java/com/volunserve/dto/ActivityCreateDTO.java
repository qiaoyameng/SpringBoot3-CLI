package com.volunserve.dto;

import com.volunserve.entity.enums.ActivityCategory;
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
public class ActivityCreateDTO {

    @NotBlank(message = "活动标题不能为空")
    @Size(max = 200, message = "活动标题不能超过200字符")
    private String title;

    @Size(max = 5000, message = "活动描述不能超过5000字符")
    private String description;

    @NotNull(message = "活动分类不能为空")
    private ActivityCategory category;

    @NotNull(message = "活动开始时间不能为空")
    @Future(message = "活动开始时间必须是未来时间")
    private LocalDateTime startTime;

    @NotNull(message = "活动结束时间不能为空")
    @Future(message = "活动结束时间必须是未来时间")
    private LocalDateTime endTime;

    @NotBlank(message = "活动地点不能为空")
    @Size(max = 300, message = "活动地点不能超过300字符")
    private String location;

    @NotNull(message = "纬度不能为空")
    private Double latitude;

    @NotNull(message = "经度不能为空")
    private Double longitude;

    @NotNull(message = "需求人数不能为空")
    @Min(value = 1, message = "需求人数至少为1")
    @Max(value = 1000, message = "需求人数不能超过1000")
    private Integer requiredVolunteers;

    @NotNull(message = "审核类型不能为空")
    private AuditType auditType;

    @Size(max = 500, message = "报名要求不能超过500字符")
    private String requirements;

    @Size(max = 500, message = "联系方式不能超过500字符")
    private String contactInfo;
}

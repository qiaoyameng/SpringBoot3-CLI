package com.volunserve.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationCreateDTO {
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String applyReason;
}

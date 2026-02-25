package com.volunserve.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCreateDTO {

    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @Size(max = 500, message = "报名留言不能超过500字符")
    private String message;
}

package com.volunserve.dto;

import com.volunserve.entity.enums.RegistrationStatus;
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
public class RegistrationReviewDTO {

    @NotNull(message = "审核状态不能为空")
    private RegistrationStatus status;

    @Size(max = 500, message = "审核备注不能超过500字符")
    private String adminComment;
}

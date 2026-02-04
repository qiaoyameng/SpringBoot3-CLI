package com.volunserve.registration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationApproveDTO {
    @NotNull(message = "报名ID不能为空")
    private Long registrationId;

    @NotNull(message = "审批状态不能为空")
    private Boolean approved;

    private String rejectReason;

    private String approvedBy;
}

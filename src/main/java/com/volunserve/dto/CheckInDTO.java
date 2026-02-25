package com.volunserve.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDTO {

    @NotNull(message = "报名ID不能为空")
    private Long registrationId;

    private Double latitude;
    private Double longitude;
}

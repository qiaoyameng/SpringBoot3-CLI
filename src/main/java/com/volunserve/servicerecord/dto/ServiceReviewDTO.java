package com.volunserve.servicerecord.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceReviewDTO {
    @NotNull(message = "服务记录ID不能为空")
    private Long serviceRecordId;

    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分最低为1星")
    @DecimalMax(value = "5.0", message = "评分最高为5星")
    private BigDecimal rating;

    private String reviewContent;
}

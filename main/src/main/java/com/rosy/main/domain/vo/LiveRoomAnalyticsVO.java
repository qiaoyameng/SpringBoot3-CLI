package com.rosy.main.domain.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LiveRoomAnalyticsVO {

    private Double conversionRate;

    private List<Map<String, Object>> retentionCurve;

    private List<Map<String, Object>> viewersCurve;

    private Integer totalAudiences;
}

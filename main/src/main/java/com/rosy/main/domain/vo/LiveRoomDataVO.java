package com.rosy.main.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class LiveRoomDataVO {

    private Integer totalOrders;

    private BigDecimal totalSales;

    private Integer totalViewers;

    private Integer peakViewers;

    private List<Map<String, Object>> productRanking;
}

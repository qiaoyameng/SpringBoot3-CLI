package com.rosy.main.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LiveRoomVO {

    private Long id;

    private String roomName;

    private String roomDesc;

    private String coverUrl;

    private Long streamerId;

    private String streamerName;

    private Byte status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer totalViewers;

    private Integer peakViewers;

    private Integer totalOrders;

    private BigDecimal totalSales;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

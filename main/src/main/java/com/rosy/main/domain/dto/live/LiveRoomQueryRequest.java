package com.rosy.main.domain.dto.live;

import lombok.Data;

@Data
public class LiveRoomQueryRequest {

    private Long current = 1L;

    private Long pageSize = 10L;

    private String roomName;

    private Byte status;

    private Long streamerId;
}

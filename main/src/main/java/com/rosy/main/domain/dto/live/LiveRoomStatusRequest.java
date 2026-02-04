package com.rosy.main.domain.dto.live;

import lombok.Data;

@Data
public class LiveRoomStatusRequest {

    private Long liveRoomId;

    private Byte status;
}

package com.rosy.main.domain.dto.live;

import lombok.Data;

@Data
public class LiveRoomUpdateRequest {

    private Long id;

    private String roomName;

    private String roomDesc;

    private String coverUrl;

    private Long streamerId;

    private String streamerName;
}

package com.rosy.main.domain.dto.liveRoomProduct;

import lombok.Data;

import java.util.List;

@Data
public class LiveRoomProductAddRequest {

    private Long liveRoomId;

    private List<Long> productIds;

    private Integer sortOrder;
}

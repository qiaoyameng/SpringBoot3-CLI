package com.rosy.main.domain.dto.liveRoomProduct;

import lombok.Data;

@Data
public class LiveRoomProductSortRequest {

    private Long liveRoomId;

    private Long productId;

    private Integer sortOrder;
}

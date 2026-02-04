package com.rosy.main.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LiveRoomProductVO {

    private Long id;

    private Long liveRoomId;

    private Long productId;

    private String productName;

    private String productDesc;

    private String sellingPoints;

    private String coverUrl;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Byte isExplaining;

    private Integer sortOrder;

    private Integer salesCount;

    private BigDecimal salesAmount;

    private LocalDateTime createTime;
}

package com.rosy.main.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductVO {

    private Long id;

    private String productName;

    private String productDesc;

    private String sellingPoints;

    private String coverUrl;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String category;

    private Byte status;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

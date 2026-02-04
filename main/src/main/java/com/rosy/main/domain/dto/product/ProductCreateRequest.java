package com.rosy.main.domain.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    private String productName;

    private String productDesc;

    private String sellingPoints;

    private String coverUrl;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String category;

    private Byte status;
}

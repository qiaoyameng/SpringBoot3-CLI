package com.rosy.main.domain.dto.product;

import lombok.Data;

@Data
public class ProductQueryRequest {

    private Long current = 1L;

    private Long pageSize = 10L;

    private String productName;

    private String category;

    private Byte status;
}

package com.rosy.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.product.*;
import com.rosy.main.domain.entity.Product;
import com.rosy.main.domain.vo.*;

import java.util.List;

public interface IProductService extends IService<Product> {

    ProductVO createProduct(ProductCreateRequest request);

    ProductVO updateProduct(ProductUpdateRequest request);

    ProductVO getProductDetail(Long id);

    PageResult<ProductVO> getProductList(ProductQueryRequest request);
}

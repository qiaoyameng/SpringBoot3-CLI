package com.rosy.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.product.*;
import com.rosy.main.domain.entity.Product;
import com.rosy.main.domain.vo.*;
import com.rosy.main.mapper.ProductMapper;
import com.rosy.main.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO createProduct(ProductCreateRequest request) {
        Product product = BeanUtil.copyProperties(request, Product.class);
        this.save(product);
        return BeanUtil.copyProperties(product, ProductVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVO updateProduct(ProductUpdateRequest request) {
        Product product = this.getById(request.getId());
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        BeanUtil.copyProperties(request, product, "id");
        this.updateById(product);
        return BeanUtil.copyProperties(product, ProductVO.class);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = this.getById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        }
        return BeanUtil.copyProperties(product, ProductVO.class);
    }

    @Override
    public PageResult<ProductVO> getProductList(ProductQueryRequest request) {
        Page<Product> page = new Page<>(request.getCurrent(), request.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (request.getStatus() != null) {
            wrapper.eq(Product::getStatus, request.getStatus());
        }
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            wrapper.eq(Product::getCategory, request.getCategory());
        }
        if (request.getProductName() != null && !request.getProductName().isEmpty()) {
            wrapper.like(Product::getProductName, request.getProductName());
        }
        wrapper.orderByDesc(Product::getCreateTime);
        IPage<Product> result = this.page(page, wrapper);
        var list = result.getRecords().stream()
                .map(item -> BeanUtil.copyProperties(item, ProductVO.class))
                .toList();
        return new PageResult<>(list, result.getTotal(), request.getCurrent(), request.getPageSize());
    }
}

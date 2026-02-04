package com.rosy.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.liveRoomProduct.*;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.entity.Product;
import com.rosy.main.domain.vo.*;
import com.rosy.main.mapper.LiveRoomProductMapper;
import com.rosy.main.mapper.ProductMapper;
import com.rosy.main.service.ILiveRoomProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LiveRoomProductServiceImpl extends ServiceImpl<LiveRoomProductMapper, LiveRoomProduct> implements ILiveRoomProductService {

    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProductsToLiveRoom(LiveRoomProductAddRequest request) {
        for (Long productId : request.getProductIds()) {
            Product product = productMapper.selectById(productId);
            if (product == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "商品不存在: " + productId);
            }
            LambdaQueryWrapper<LiveRoomProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LiveRoomProduct::getLiveRoomId, request.getLiveRoomId())
                    .eq(LiveRoomProduct::getProductId, productId);
            LiveRoomProduct existing = this.getOne(wrapper);
            if (existing == null) {
                LiveRoomProduct liveRoomProduct = new LiveRoomProduct();
                liveRoomProduct.setLiveRoomId(request.getLiveRoomId());
                liveRoomProduct.setProductId(productId);
                liveRoomProduct.setIsExplaining((byte) 0);
                liveRoomProduct.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
                liveRoomProduct.setSalesCount(0);
                this.save(liveRoomProduct);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeProductFromLiveRoom(Long liveRoomId, Long productId) {
        LambdaQueryWrapper<LiveRoomProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LiveRoomProduct::getLiveRoomId, liveRoomId)
                .eq(LiveRoomProduct::getProductId, productId);
        return this.remove(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProductSortOrder(LiveRoomProductSortRequest request) {
        LambdaQueryWrapper<LiveRoomProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LiveRoomProduct::getLiveRoomId, request.getLiveRoomId())
                .eq(LiveRoomProduct::getProductId, request.getProductId());
        LiveRoomProduct product = this.getOne(wrapper);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间商品不存在");
        }
        product.setSortOrder(request.getSortOrder());
        return this.updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean switchExplainingProduct(Long liveRoomId, Long productId) {
        LambdaQueryWrapper<LiveRoomProduct> resetWrapper = new LambdaQueryWrapper<>();
        resetWrapper.eq(LiveRoomProduct::getLiveRoomId, liveRoomId);
        List<LiveRoomProduct> products = this.list(resetWrapper);
        for (LiveRoomProduct p : products) {
            p.setIsExplaining((byte) 0);
        }
        this.updateBatchById(products);
        if (productId != null) {
            LambdaQueryWrapper<LiveRoomProduct> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LiveRoomProduct::getLiveRoomId, liveRoomId)
                    .eq(LiveRoomProduct::getProductId, productId);
            LiveRoomProduct product = this.getOne(wrapper);
            if (product != null) {
                product.setIsExplaining((byte) 1);
                this.updateById(product);
            }
        }
        return true;
    }

    @Override
    public List<LiveRoomProductVO> getLiveRoomProducts(Long liveRoomId) {
        List<LiveRoomProduct> products = baseMapper.selectByLiveRoomId(liveRoomId);
        return products.stream().map(p -> {
            LiveRoomProductVO vo = BeanUtil.copyProperties(p, LiveRoomProductVO.class);
            Product product = productMapper.selectById(p.getProductId());
            if (product != null) {
                vo.setProductName(product.getProductName());
                vo.setProductDesc(product.getProductDesc());
                vo.setSellingPoints(product.getSellingPoints());
                vo.setCoverUrl(product.getCoverUrl());
                vo.setPrice(product.getPrice());
                vo.setOriginalPrice(product.getOriginalPrice());
                vo.setStock(product.getStock());
            }
            return vo;
        }).toList();
    }

    @Override
    public LiveRoomProductVO getExplainingProduct(Long liveRoomId) {
        LiveRoomProduct product = baseMapper.selectExplainingProduct(liveRoomId);
        return Optional.ofNullable(product).map(p -> {
            LiveRoomProductVO vo = BeanUtil.copyProperties(p, LiveRoomProductVO.class);
            Product prod = productMapper.selectById(p.getProductId());
            if (prod != null) {
                vo.setProductName(prod.getProductName());
                vo.setProductDesc(prod.getProductDesc());
                vo.setSellingPoints(prod.getSellingPoints());
                vo.setCoverUrl(prod.getCoverUrl());
                vo.setPrice(prod.getPrice());
                vo.setOriginalPrice(prod.getOriginalPrice());
                vo.setStock(prod.getStock());
            }
            return vo;
        }).orElse(null);
    }
}

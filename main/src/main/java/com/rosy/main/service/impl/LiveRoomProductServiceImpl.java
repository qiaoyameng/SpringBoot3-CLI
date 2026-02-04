package com.rosy.main.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.QueryWrapperUtil;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductQueryRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductUpdateRequest;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.vo.LiveRoomProductVO;
import com.rosy.main.mapper.LiveRoomProductMapper;
import com.rosy.main.service.ILiveRoomProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播间商品关联表 服务实现类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Service
public class LiveRoomProductServiceImpl extends ServiceImpl<LiveRoomProductMapper, LiveRoomProduct> implements ILiveRoomProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addLiveRoomProduct(LiveRoomProductAddRequest liveRoomProductAddRequest) {
        LiveRoomProduct liveRoomProduct = new LiveRoomProduct();
        BeanUtils.copyProperties(liveRoomProductAddRequest, liveRoomProduct);

        // 卖点列表转JSON
        if (liveRoomProductAddRequest.getSellingPoints() != null) {
            liveRoomProduct.setSellingPoints(JSONUtil.toJsonStr(liveRoomProductAddRequest.getSellingPoints()));
        }

        liveRoomProduct.setStatus((byte) 1);
        liveRoomProduct.setSalesCount(0);

        boolean result = this.save(liveRoomProduct);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return liveRoomProduct.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLiveRoomProduct(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLiveRoomProduct(LiveRoomProductUpdateRequest liveRoomProductUpdateRequest) {
        LiveRoomProduct liveRoomProduct = new LiveRoomProduct();
        BeanUtils.copyProperties(liveRoomProductUpdateRequest, liveRoomProduct);

        // 卖点列表转JSON
        if (liveRoomProductUpdateRequest.getSellingPoints() != null) {
            liveRoomProduct.setSellingPoints(JSONUtil.toJsonStr(liveRoomProductUpdateRequest.getSellingPoints()));
        }

        boolean result = this.updateById(liveRoomProduct);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public LiveRoomProductVO getLiveRoomProductById(Long id) {
        LiveRoomProduct liveRoomProduct = this.getById(id);
        if (liveRoomProduct == null) {
            return null;
        }
        return getLiveRoomProductVO(liveRoomProduct);
    }

    @Override
    public List<LiveRoomProductVO> getProductsByLiveRoomId(Long liveRoomId) {
        List<LiveRoomProduct> list = baseMapper.selectByLiveRoomId(liveRoomId);
        return getLiveRoomProductVOList(list);
    }

    @Override
    public Page<LiveRoomProductVO> pageLiveRoomProduct(LiveRoomProductQueryRequest liveRoomProductQueryRequest) {
        long current = liveRoomProductQueryRequest.getCurrent();
        long size = liveRoomProductQueryRequest.getPageSize();
        LambdaQueryWrapper<LiveRoomProduct> queryWrapper = getQueryWrapper(liveRoomProductQueryRequest);
        Page<LiveRoomProduct> page = this.page(new Page<>(current, size), queryWrapper);
        List<LiveRoomProductVO> voList = getLiveRoomProductVOList(page.getRecords());
        Page<LiveRoomProductVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<LiveRoomProductVO> getProductRanking(Long liveRoomId, int limit) {
        List<LiveRoomProduct> list = baseMapper.selectProductRanking(liveRoomId, limit);
        return getLiveRoomProductVOList(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean increaseSalesCount(Long productId, int count) {
        return baseMapper.increaseSalesCount(productId, count, null) > 0;
    }

    @Override
    public LambdaQueryWrapper<LiveRoomProduct> getQueryWrapper(LiveRoomProductQueryRequest liveRoomProductQueryRequest) {
        if (liveRoomProductQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        LambdaQueryWrapper<LiveRoomProduct> queryWrapper = new LambdaQueryWrapper<>();

        QueryWrapperUtil.addCondition(queryWrapper, liveRoomProductQueryRequest.getId(), LiveRoomProduct::getId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomProductQueryRequest.getLiveRoomId(), LiveRoomProduct::getLiveRoomId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomProductQueryRequest.getProductId(), LiveRoomProduct::getProductId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomProductQueryRequest.getProductName(), LiveRoomProduct::getProductName);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomProductQueryRequest.getStatus(), LiveRoomProduct::getStatus);

        QueryWrapperUtil.addSortCondition(queryWrapper,
                liveRoomProductQueryRequest.getSortField(),
                liveRoomProductQueryRequest.getSortOrder(),
                LiveRoomProduct::getSortOrder);

        return queryWrapper;
    }

    @Override
    public LiveRoomProductVO getLiveRoomProductVO(LiveRoomProduct liveRoomProduct) {
        if (liveRoomProduct == null) {
            return null;
        }
        LiveRoomProductVO vo = new LiveRoomProductVO();
        BeanUtils.copyProperties(liveRoomProduct, vo);

        // 设置状态文本
        vo.setStatusText(getStatusText(liveRoomProduct.getStatus()));

        // JSON转卖点列表
        if (liveRoomProduct.getSellingPoints() != null) {
            try {
                List<String> sellingPoints = JSONUtil.toList(liveRoomProduct.getSellingPoints(), String.class);
                vo.setSellingPoints(sellingPoints);
            } catch (Exception e) {
                vo.setSellingPoints(List.of());
            }
        }

        return vo;
    }

    @Override
    public List<LiveRoomProductVO> getLiveRoomProductVOList(List<LiveRoomProduct> liveRoomProductList) {
        return liveRoomProductList.stream()
                .map(this::getLiveRoomProductVO)
                .collect(Collectors.toList());
    }

    private String getStatusText(Byte status) {
        if (status == null) {
            return "未知";
        }
        return status == 1 ? "上架" : "下架";
    }
}

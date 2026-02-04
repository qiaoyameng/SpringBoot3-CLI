package com.rosy.main.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.QueryWrapperUtil;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.LiveOrderAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveOrderQueryRequest;
import com.rosy.main.domain.entity.LiveOrder;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.vo.LiveOrderVO;
import com.rosy.main.mapper.LiveOrderMapper;
import com.rosy.main.mapper.LiveRoomMapper;
import com.rosy.main.service.ILiveOrderService;
import com.rosy.main.service.ILiveRoomProductService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播间订单表 服务实现类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Service
public class LiveOrderServiceImpl extends ServiceImpl<LiveOrderMapper, LiveOrder> implements ILiveOrderService {

    @Resource
    private ILiveRoomProductService liveRoomProductService;

    @Resource
    private LiveRoomMapper liveRoomMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addLiveOrder(LiveOrderAddRequest liveOrderAddRequest) {
        // 获取商品信息
        LiveRoomProduct product = liveRoomProductService.getById(liveOrderAddRequest.getProductId());
        ThrowUtils.throwIf(product == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        ThrowUtils.throwIf(product.getStock() <= 0, ErrorCode.PARAMS_ERROR, "商品库存不足");

        LiveOrder liveOrder = new LiveOrder();
        BeanUtils.copyProperties(liveOrderAddRequest, liveOrder);
        liveOrder.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        liveOrder.setUnitPrice(product.getProductPrice());

        // 计算总价
        int quantity = liveOrderAddRequest.getQuantity() != null ? liveOrderAddRequest.getQuantity() : 1;
        liveOrder.setQuantity(quantity);
        liveOrder.setTotalAmount(product.getProductPrice().multiply(BigDecimal.valueOf(quantity)));
        liveOrder.setStatus((byte) 0);

        boolean result = this.save(liveOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return liveOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLiveOrder(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long id) {
        LiveOrder liveOrder = this.getById(id);
        ThrowUtils.throwIf(liveOrder == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(liveOrder.getStatus() != 0, ErrorCode.PARAMS_ERROR, "订单状态不正确");

        // 更新订单状态
        LiveOrder updateOrder = new LiveOrder();
        updateOrder.setId(id);
        updateOrder.setStatus((byte) 1);
        updateOrder.setPayTime(LocalDateTime.now());
        boolean result = this.updateById(updateOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 增加商品销量
        liveRoomProductService.increaseSalesCount(liveOrder.getProductId(), liveOrder.getQuantity());

        // 更新直播间销售数据
        liveRoomMapper.updateSalesData(liveOrder.getLiveRoomId(), 1, liveOrder.getTotalAmount(), null);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long id) {
        LiveOrder liveOrder = this.getById(id);
        ThrowUtils.throwIf(liveOrder == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(liveOrder.getStatus() != 0, ErrorCode.PARAMS_ERROR, "订单状态不正确");

        LiveOrder updateOrder = new LiveOrder();
        updateOrder.setId(id);
        updateOrder.setStatus((byte) 4);
        boolean result = this.updateById(updateOrder);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public LiveOrderVO getLiveOrderById(Long id) {
        LiveOrder liveOrder = this.getById(id);
        if (liveOrder == null) {
            return null;
        }
        return getLiveOrderVO(liveOrder);
    }

    @Override
    public Page<LiveOrderVO> pageLiveOrder(LiveOrderQueryRequest liveOrderQueryRequest) {
        long current = liveOrderQueryRequest.getCurrent();
        long size = liveOrderQueryRequest.getPageSize();
        LambdaQueryWrapper<LiveOrder> queryWrapper = getQueryWrapper(liveOrderQueryRequest);
        Page<LiveOrder> page = this.page(new Page<>(current, size), queryWrapper);
        List<LiveOrderVO> voList = getLiveOrderVOList(page.getRecords());
        Page<LiveOrderVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<LiveOrderVO> getOrdersByLiveRoomId(Long liveRoomId) {
        LambdaQueryWrapper<LiveOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiveOrder::getLiveRoomId, liveRoomId);
        queryWrapper.orderByDesc(LiveOrder::getCreateTime);
        List<LiveOrder> list = this.list(queryWrapper);
        return getLiveOrderVOList(list);
    }

    @Override
    public Map<String, Object> getOrderStatsByLiveRoomId(Long liveRoomId) {
        return baseMapper.selectStatsByLiveRoomId(liveRoomId);
    }

    @Override
    public List<Map<String, Object>> getProductSalesStats(Long liveRoomId) {
        return baseMapper.selectProductSalesStats(liveRoomId);
    }

    @Override
    public BigDecimal getTotalSalesAmount(Long liveRoomId) {
        return baseMapper.selectTotalSalesAmount(liveRoomId);
    }

    @Override
    public int getPaidOrderCount(Long liveRoomId) {
        return baseMapper.selectPaidOrderCount(liveRoomId);
    }

    @Override
    public LambdaQueryWrapper<LiveOrder> getQueryWrapper(LiveOrderQueryRequest liveOrderQueryRequest) {
        if (liveOrderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        LambdaQueryWrapper<LiveOrder> queryWrapper = new LambdaQueryWrapper<>();

        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getId(), LiveOrder::getId);
        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getOrderNo(), LiveOrder::getOrderNo);
        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getLiveRoomId(), LiveOrder::getLiveRoomId);
        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getProductId(), LiveOrder::getProductId);
        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getUserId(), LiveOrder::getUserId);
        QueryWrapperUtil.addCondition(queryWrapper, liveOrderQueryRequest.getStatus(), LiveOrder::getStatus);

        // 时间范围
        if (liveOrderQueryRequest.getCreateTimeStart() != null) {
            queryWrapper.ge(LiveOrder::getCreateTime, liveOrderQueryRequest.getCreateTimeStart());
        }
        if (liveOrderQueryRequest.getCreateTimeEnd() != null) {
            queryWrapper.le(LiveOrder::getCreateTime, liveOrderQueryRequest.getCreateTimeEnd());
        }

        QueryWrapperUtil.addSortCondition(queryWrapper,
                liveOrderQueryRequest.getSortField(),
                liveOrderQueryRequest.getSortOrder(),
                LiveOrder::getCreateTime);

        return queryWrapper;
    }

    @Override
    public LiveOrderVO getLiveOrderVO(LiveOrder liveOrder) {
        if (liveOrder == null) {
            return null;
        }
        LiveOrderVO vo = new LiveOrderVO();
        BeanUtils.copyProperties(liveOrder, vo);
        vo.setStatusText(getStatusText(liveOrder.getStatus()));
        return vo;
    }

    @Override
    public List<LiveOrderVO> getLiveOrderVOList(List<LiveOrder> liveOrderList) {
        return liveOrderList.stream()
                .map(this::getLiveOrderVO)
                .collect(Collectors.toList());
    }

    private String getStatusText(Byte status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}

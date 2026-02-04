package com.rosy.main.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.liveroom.LiveOrderAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveOrderQueryRequest;
import com.rosy.main.domain.entity.LiveOrder;
import com.rosy.main.domain.vo.LiveOrderVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间订单表 服务类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface ILiveOrderService extends IService<LiveOrder> {

    /**
     * 创建订单
     */
    Long addLiveOrder(LiveOrderAddRequest liveOrderAddRequest);

    /**
     * 删除订单
     */
    boolean deleteLiveOrder(Long id);

    /**
     * 支付订单
     */
    boolean payOrder(Long id);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long id);

    /**
     * 根据ID获取订单
     */
    LiveOrderVO getLiveOrderById(Long id);

    /**
     * 分页查询订单
     */
    Page<LiveOrderVO> pageLiveOrder(LiveOrderQueryRequest liveOrderQueryRequest);

    /**
     * 获取直播间订单列表
     */
    List<LiveOrderVO> getOrdersByLiveRoomId(Long liveRoomId);

    /**
     * 统计直播间订单数据
     */
    Map<String, Object> getOrderStatsByLiveRoomId(Long liveRoomId);

    /**
     * 获取直播间商品销售统计
     */
    List<Map<String, Object>> getProductSalesStats(Long liveRoomId);

    /**
     * 获取直播间总销售额
     */
    BigDecimal getTotalSalesAmount(Long liveRoomId);

    /**
     * 获取直播间已支付订单数
     */
    int getPaidOrderCount(Long liveRoomId);

    /**
     * 获取查询条件包装器
     */
    LambdaQueryWrapper<LiveOrder> getQueryWrapper(LiveOrderQueryRequest liveOrderQueryRequest);

    /**
     * 实体转VO
     */
    LiveOrderVO getLiveOrderVO(LiveOrder liveOrder);

    /**
     * 实体列表转VO列表
     */
    List<LiveOrderVO> getLiveOrderVOList(List<LiveOrder> liveOrderList);
}

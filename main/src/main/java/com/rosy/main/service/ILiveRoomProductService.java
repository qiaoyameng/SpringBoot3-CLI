package com.rosy.main.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductQueryRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductUpdateRequest;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.vo.LiveRoomProductVO;

import java.util.List;

/**
 * <p>
 * 直播间商品关联表 服务类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface ILiveRoomProductService extends IService<LiveRoomProduct> {

    /**
     * 添加直播间商品
     */
    Long addLiveRoomProduct(LiveRoomProductAddRequest liveRoomProductAddRequest);

    /**
     * 删除直播间商品
     */
    boolean deleteLiveRoomProduct(Long id);

    /**
     * 更新直播间商品
     */
    boolean updateLiveRoomProduct(LiveRoomProductUpdateRequest liveRoomProductUpdateRequest);

    /**
     * 根据ID获取直播间商品
     */
    LiveRoomProductVO getLiveRoomProductById(Long id);

    /**
     * 根据直播间ID获取商品列表
     */
    List<LiveRoomProductVO> getProductsByLiveRoomId(Long liveRoomId);

    /**
     * 分页查询直播间商品
     */
    Page<LiveRoomProductVO> pageLiveRoomProduct(LiveRoomProductQueryRequest liveRoomProductQueryRequest);

    /**
     * 获取商品排行榜
     */
    List<LiveRoomProductVO> getProductRanking(Long liveRoomId, int limit);

    /**
     * 增加商品销量
     */
    boolean increaseSalesCount(Long productId, int count);

    /**
     * 获取查询条件包装器
     */
    LambdaQueryWrapper<LiveRoomProduct> getQueryWrapper(LiveRoomProductQueryRequest liveRoomProductQueryRequest);

    /**
     * 实体转VO
     */
    LiveRoomProductVO getLiveRoomProductVO(LiveRoomProduct liveRoomProduct);

    /**
     * 实体列表转VO列表
     */
    List<LiveRoomProductVO> getLiveRoomProductVOList(List<LiveRoomProduct> liveRoomProductList);
}

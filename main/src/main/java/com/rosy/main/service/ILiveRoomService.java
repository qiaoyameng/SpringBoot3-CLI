package com.rosy.main.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.liveroom.*;
import com.rosy.main.domain.entity.LiveRoom;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;
import com.rosy.main.domain.vo.LiveRoomVO;

import java.util.List;

/**
 * <p>
 * 直播间表 服务类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface ILiveRoomService extends IService<LiveRoom> {

    /**
     * 创建直播间
     */
    Long addLiveRoom(LiveRoomAddRequest liveRoomAddRequest);

    /**
     * 删除直播间
     */
    boolean deleteLiveRoom(Long id);

    /**
     * 更新直播间
     */
    boolean updateLiveRoom(LiveRoomUpdateRequest liveRoomUpdateRequest);

    /**
     * 更新直播间状态
     */
    boolean updateLiveRoomStatus(LiveRoomStatusUpdateRequest liveRoomStatusUpdateRequest);

    /**
     * 切换讲解商品
     */
    boolean switchCurrentProduct(LiveRoomSwitchProductRequest liveRoomSwitchProductRequest);

    /**
     * 根据ID获取直播间
     */
    LiveRoomVO getLiveRoomById(Long id);

    /**
     * 分页查询直播间
     */
    Page<LiveRoomVO> pageLiveRoom(LiveRoomQueryRequest liveRoomQueryRequest);

    /**
     * 获取直播间列表
     */
    List<LiveRoomVO> listLiveRoom(LiveRoomQueryRequest liveRoomQueryRequest);

    /**
     * 获取直播间数据分析
     */
    LiveDataAnalysisVO getLiveDataAnalysis(Long liveRoomId);

    /**
     * 获取查询条件包装器
     */
    LambdaQueryWrapper<LiveRoom> getQueryWrapper(LiveRoomQueryRequest liveRoomQueryRequest);

    /**
     * 实体转VO
     */
    LiveRoomVO getLiveRoomVO(LiveRoom liveRoom);

    /**
     * 实体列表转VO列表
     */
    List<LiveRoomVO> getLiveRoomVOList(List<LiveRoom> liveRoomList);
}

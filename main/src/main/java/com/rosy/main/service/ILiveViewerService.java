package com.rosy.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.liveroom.LiveViewerEnterRequest;
import com.rosy.main.domain.dto.liveroom.LiveViewerLeaveRequest;
import com.rosy.main.domain.entity.LiveViewer;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间观众记录表 服务类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface ILiveViewerService extends IService<LiveViewer> {

    /**
     * 观众进入直播间
     */
    Long viewerEnter(LiveViewerEnterRequest liveViewerEnterRequest);

    /**
     * 观众离开直播间
     */
    boolean viewerLeave(LiveViewerLeaveRequest liveViewerLeaveRequest);

    /**
     * 统计直播间观众数据
     */
    Map<String, Object> getViewerStats(Long liveRoomId);

    /**
     * 获取直播间峰值观看人数
     */
    int getPeakViewerCount(Long liveRoomId);

    /**
     * 获取直播间平均观看时长
     */
    int getAvgViewDuration(Long liveRoomId);

    /**
     * 获取直播间独立访客数
     */
    int getUniqueViewerCount(Long liveRoomId);

    /**
     * 获取观众留存数据
     */
    List<LiveDataAnalysisVO.ViewerRetentionVO> getRetentionData(Long liveRoomId);

    /**
     * 获取直播间当前在线人数
     */
    int getCurrentViewerCount(Long liveRoomId);
}

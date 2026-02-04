package com.rosy.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.LiveViewerEnterRequest;
import com.rosy.main.domain.dto.liveroom.LiveViewerLeaveRequest;
import com.rosy.main.domain.entity.LiveRoom;
import com.rosy.main.domain.entity.LiveViewer;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;
import com.rosy.main.mapper.LiveRoomMapper;
import com.rosy.main.mapper.LiveViewerMapper;
import com.rosy.main.service.ILiveViewerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播间观众记录表 服务实现类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Service
public class LiveViewerServiceImpl extends ServiceImpl<LiveViewerMapper, LiveViewer> implements ILiveViewerService {

    @Resource
    private LiveRoomMapper liveRoomMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long viewerEnter(LiveViewerEnterRequest liveViewerEnterRequest) {
        // 验证直播间是否存在
        LiveRoom liveRoom = liveRoomMapper.selectById(liveViewerEnterRequest.getLiveRoomId());
        ThrowUtils.throwIf(liveRoom == null, ErrorCode.NOT_FOUND_ERROR, "直播间不存在");

        // 创建观众记录
        LiveViewer liveViewer = new LiveViewer();
        liveViewer.setLiveRoomId(liveViewerEnterRequest.getLiveRoomId());
        liveViewer.setUserId(liveViewerEnterRequest.getUserId());
        liveViewer.setSessionId(liveViewerEnterRequest.getSessionId());
        liveViewer.setEnterTime(LocalDateTime.now());
        liveViewer.setIpAddress(liveViewerEnterRequest.getIpAddress());

        boolean result = this.save(liveViewer);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 增加直播间观看人数
        liveRoomMapper.increaseViewerCount(liveViewerEnterRequest.getLiveRoomId(), 1, null);

        return liveViewer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean viewerLeave(LiveViewerLeaveRequest liveViewerLeaveRequest) {
        // 查询观众记录
        LiveViewer liveViewer = baseMapper.selectByLiveRoomIdAndSessionId(
                liveViewerLeaveRequest.getLiveRoomId(),
                liveViewerLeaveRequest.getSessionId()
        );

        if (liveViewer == null) {
            return false;
        }

        // 计算观看时长
        LocalDateTime leaveTime = LocalDateTime.now();
        int duration = (int) ChronoUnit.SECONDS.between(liveViewer.getEnterTime(), leaveTime);

        // 更新观众记录
        baseMapper.updateLeaveInfo(liveViewer.getId(), leaveTime, duration, null);

        // 减少直播间观看人数
        liveRoomMapper.decreaseViewerCount(liveViewerLeaveRequest.getLiveRoomId(), 1, null);

        return true;
    }

    @Override
    public Map<String, Object> getViewerStats(Long liveRoomId) {
        return baseMapper.selectStatsByLiveRoomId(liveRoomId);
    }

    @Override
    public int getPeakViewerCount(Long liveRoomId) {
        // 从直播间统计表中获取峰值观看人数
        // 这里简化处理，实际应该从LiveRoomStatsMapper查询
        return 0;
    }

    @Override
    public int getAvgViewDuration(Long liveRoomId) {
        Map<String, Object> stats = baseMapper.selectStatsByLiveRoomId(liveRoomId);
        Object avgDuration = stats.get("avg_duration");
        if (avgDuration == null) {
            return 0;
        }
        return ((Number) avgDuration).intValue();
    }

    @Override
    public int getUniqueViewerCount(Long liveRoomId) {
        return baseMapper.countUniqueViewers(liveRoomId);
    }

    @Override
    public List<LiveDataAnalysisVO.ViewerRetentionVO> getRetentionData(Long liveRoomId) {
        List<Map<String, Object>> retentionData = baseMapper.selectRetentionData(liveRoomId);

        if (retentionData.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取总观众数
        Map<String, Object> stats = baseMapper.selectStatsByLiveRoomId(liveRoomId);
        int totalViewers = ((Number) stats.get("total_viewers")).intValue();

        if (totalViewers == 0) {
            return new ArrayList<>();
        }

        // 构建留存曲线数据
        List<LiveDataAnalysisVO.ViewerRetentionVO> result = new ArrayList<>();

        for (Map<String, Object> data : retentionData) {
            Integer timePoint = ((Number) data.get("time_point")).intValue();
            Integer retentionCount = ((Number) data.get("retention_count")).intValue();

            LiveDataAnalysisVO.ViewerRetentionVO vo = new LiveDataAnalysisVO.ViewerRetentionVO();
            vo.setTimePoint(timePoint);
            vo.setRetentionCount(retentionCount);

            // 计算留存率
            BigDecimal retentionRate = BigDecimal.valueOf(retentionCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalViewers), 2, RoundingMode.HALF_UP);
            vo.setRetentionRate(retentionRate);

            result.add(vo);
        }

        return result;
    }

    @Override
    public int getCurrentViewerCount(Long liveRoomId) {
        LiveRoom liveRoom = liveRoomMapper.selectById(liveRoomId);
        if (liveRoom == null) {
            return 0;
        }
        return liveRoom.getViewerCount() != null ? liveRoom.getViewerCount() : 0;
    }
}

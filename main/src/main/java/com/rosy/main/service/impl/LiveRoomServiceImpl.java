package com.rosy.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.live.*;
import com.rosy.main.domain.entity.*;
import com.rosy.main.domain.vo.*;
import com.rosy.main.mapper.*;
import com.rosy.main.service.ILiveRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom> implements ILiveRoomService {

    private final LiveOrderMapper liveOrderMapper;
    private final AudienceRecordMapper audienceRecordMapper;
    private final ViewersStatsMinuteMapper viewersStatsMinuteMapper;
    private final LiveRoomProductMapper liveRoomProductMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LiveRoomVO createLiveRoom(LiveRoomCreateRequest request) {
        LiveRoom liveRoom = BeanUtil.copyProperties(request, LiveRoom.class);
        liveRoom.setStatus((byte) 0);
        liveRoom.setTotalViewers(0);
        liveRoom.setPeakViewers(0);
        liveRoom.setTotalOrders(0);
        liveRoom.setTotalSales(BigDecimal.ZERO);
        this.save(liveRoom);
        return BeanUtil.copyProperties(liveRoom, LiveRoomVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LiveRoomVO updateLiveRoom(LiveRoomUpdateRequest request) {
        LiveRoom liveRoom = this.getById(request.getId());
        if (liveRoom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间不存在");
        }
        BeanUtil.copyProperties(request, liveRoom, "id");
        this.updateById(liveRoom);
        return BeanUtil.copyProperties(liveRoom, LiveRoomVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(LiveRoomStatusRequest request) {
        LiveRoom liveRoom = this.getById(request.getLiveRoomId());
        if (liveRoom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间不存在");
        }
        liveRoom.setStatus(request.getStatus());
        if (request.getStatus() == 1) {
            liveRoom.setStartTime(LocalDateTime.now());
        } else if (request.getStatus() == 2) {
            liveRoom.setEndTime(LocalDateTime.now());
        }
        return this.updateById(liveRoom);
    }

    @Override
    public LiveRoomVO getLiveRoomDetail(Long id) {
        LiveRoom liveRoom = this.getById(id);
        if (liveRoom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间不存在");
        }
        return BeanUtil.copyProperties(liveRoom, LiveRoomVO.class);
    }

    @Override
    public PageResult<LiveRoomVO> getLiveRoomList(LiveRoomQueryRequest request) {
        Page<LiveRoom> page = new Page<>(request.getCurrent(), request.getPageSize());
        LambdaQueryWrapper<LiveRoom> wrapper = new LambdaQueryWrapper<>();
        if (request.getStatus() != null) {
            wrapper.eq(LiveRoom::getStatus, request.getStatus());
        }
        if (request.getStreamerId() != null) {
            wrapper.eq(LiveRoom::getStreamerId, request.getStreamerId());
        }
        if (request.getRoomName() != null && !request.getRoomName().isEmpty()) {
            wrapper.like(LiveRoom::getRoomName, request.getRoomName());
        }
        wrapper.orderByDesc(LiveRoom::getCreateTime);
        IPage<LiveRoom> result = this.page(page, wrapper);
        List<LiveRoomVO> list = result.getRecords().stream()
                .map(item -> BeanUtil.copyProperties(item, LiveRoomVO.class))
                .toList();
        return new PageResult<>(list, result.getTotal(), request.getCurrent(), request.getPageSize());
    }

    @Override
    public LiveRoomDataVO getLiveRoomData(Long liveRoomId) {
        LiveRoom liveRoom = this.getById(liveRoomId);
        if (liveRoom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间不存在");
        }
        LiveRoomDataVO vo = new LiveRoomDataVO();
        vo.setTotalOrders(liveRoom.getTotalOrders());
        vo.setTotalSales(liveRoom.getTotalSales());
        vo.setTotalViewers(liveRoom.getTotalViewers());
        vo.setPeakViewers(liveRoom.getPeakViewers());
        List<Map<String, Object>> ranking = liveOrderMapper.selectProductSalesRanking(liveRoomId);
        vo.setProductRanking(ranking);
        return vo;
    }

    @Override
    public LiveRoomAnalyticsVO getLiveRoomAnalytics(Long liveRoomId) {
        LiveRoom liveRoom = this.getById(liveRoomId);
        if (liveRoom == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "直播间不存在");
        }
        LiveRoomAnalyticsVO vo = new LiveRoomAnalyticsVO();
        Double conversionRate = audienceRecordMapper.selectConversionRate(liveRoomId);
        vo.setConversionRate(conversionRate);
        List<Map<String, Object>> retentionData = audienceRecordMapper.selectRetentionData(liveRoomId);
        vo.setRetentionCurve(retentionData);
        List<ViewersStatsMinute> viewersStats = viewersStatsMinuteMapper.selectByLiveRoomId(liveRoomId);
        List<Map<String, Object>> viewersCurve = new ArrayList<>();
        for (ViewersStatsMinute stat : viewersStats) {
            Map<String, Object> point = new HashMap<>();
            point.put("time", stat.getStatsTime());
            point.put("viewers", stat.getViewersCount());
            viewersCurve.add(point);
        }
        vo.setViewersCurve(viewersCurve);
        Integer totalAudiences = audienceRecordMapper.selectTotalAudiences(liveRoomId);
        vo.setTotalAudiences(totalAudiences);
        return vo;
    }
}

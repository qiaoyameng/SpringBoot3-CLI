package com.rosy.web.controller.main;

import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.liveroom.LiveViewerEnterRequest;
import com.rosy.main.domain.dto.liveroom.LiveViewerLeaveRequest;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;
import com.rosy.main.service.ILiveViewerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间观众管理 前端控制器
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/liveViewer")
public class LiveViewerController {

    @Resource
    private ILiveViewerService liveViewerService;

    /**
     * 观众进入直播间
     */
    @PostMapping("/enter")
    @ValidateRequest
    public ApiResponse viewerEnter(@RequestBody LiveViewerEnterRequest liveViewerEnterRequest) {
        Long id = liveViewerService.viewerEnter(liveViewerEnterRequest);
        return ApiResponse.success(id);
    }

    /**
     * 观众离开直播间
     */
    @PostMapping("/leave")
    @ValidateRequest
    public ApiResponse viewerLeave(@RequestBody LiveViewerLeaveRequest liveViewerLeaveRequest) {
        boolean result = liveViewerService.viewerLeave(liveViewerLeaveRequest);
        return ApiResponse.success(result);
    }

    /**
     * 获取直播间观众统计
     */
    @GetMapping("/stats")
    public ApiResponse getViewerStats(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Object> stats = liveViewerService.getViewerStats(liveRoomId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取直播间峰值观看人数
     */
    @GetMapping("/peakViewerCount")
    public ApiResponse getPeakViewerCount(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int count = liveViewerService.getPeakViewerCount(liveRoomId);
        return ApiResponse.success(count);
    }

    /**
     * 获取直播间平均观看时长
     */
    @GetMapping("/avgViewDuration")
    public ApiResponse getAvgViewDuration(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int duration = liveViewerService.getAvgViewDuration(liveRoomId);
        return ApiResponse.success(duration);
    }

    /**
     * 获取直播间独立访客数
     */
    @GetMapping("/uniqueViewerCount")
    public ApiResponse getUniqueViewerCount(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int count = liveViewerService.getUniqueViewerCount(liveRoomId);
        return ApiResponse.success(count);
    }

    /**
     * 获取观众留存数据
     */
    @GetMapping("/retentionData")
    public ApiResponse getRetentionData(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<LiveDataAnalysisVO.ViewerRetentionVO> list = liveViewerService.getRetentionData(liveRoomId);
        return ApiResponse.success(list);
    }

    /**
     * 获取直播间当前在线人数
     */
    @GetMapping("/currentViewerCount")
    public ApiResponse getCurrentViewerCount(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int count = liveViewerService.getCurrentViewerCount(liveRoomId);
        return ApiResponse.success(count);
    }
}

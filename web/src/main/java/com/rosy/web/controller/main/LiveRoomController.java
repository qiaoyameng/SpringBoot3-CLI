package com.rosy.web.controller.main;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.domain.entity.IdRequest;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.*;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;
import com.rosy.main.domain.vo.LiveRoomVO;
import com.rosy.main.service.ILiveRoomService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 直播间管理 前端控制器
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/liveRoom")
public class LiveRoomController {

    @Resource
    private ILiveRoomService liveRoomService;

    // region 直播间管理

    /**
     * 创建直播间
     */
    @PostMapping("/add")
    @ValidateRequest
    public ApiResponse addLiveRoom(@RequestBody LiveRoomAddRequest liveRoomAddRequest) {
        Long id = liveRoomService.addLiveRoom(liveRoomAddRequest);
        return ApiResponse.success(id);
    }

    /**
     * 删除直播间
     */
    @PostMapping("/delete")
    @ValidateRequest
    public ApiResponse deleteLiveRoom(@RequestBody IdRequest idRequest) {
        boolean result = liveRoomService.deleteLiveRoom(idRequest.getId());
        return ApiResponse.success(result);
    }

    /**
     * 更新直播间
     */
    @PostMapping("/update")
    @ValidateRequest
    public ApiResponse updateLiveRoom(@RequestBody LiveRoomUpdateRequest liveRoomUpdateRequest) {
        boolean result = liveRoomService.updateLiveRoom(liveRoomUpdateRequest);
        return ApiResponse.success(result);
    }

    /**
     * 更新直播间状态
     */
    @PostMapping("/updateStatus")
    @ValidateRequest
    public ApiResponse updateLiveRoomStatus(@RequestBody LiveRoomStatusUpdateRequest liveRoomStatusUpdateRequest) {
        boolean result = liveRoomService.updateLiveRoomStatus(liveRoomStatusUpdateRequest);
        return ApiResponse.success(result);
    }

    /**
     * 切换讲解商品
     */
    @PostMapping("/switchProduct")
    @ValidateRequest
    public ApiResponse switchCurrentProduct(@RequestBody LiveRoomSwitchProductRequest liveRoomSwitchProductRequest) {
        boolean result = liveRoomService.switchCurrentProduct(liveRoomSwitchProductRequest);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取直播间
     */
    @GetMapping("/get")
    public ApiResponse getLiveRoomById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LiveRoomVO liveRoomVO = liveRoomService.getLiveRoomById(id);
        ThrowUtils.throwIf(liveRoomVO == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResponse.success(liveRoomVO);
    }

    /**
     * 分页查询直播间
     */
    @PostMapping("/list/page")
    @ValidateRequest
    public ApiResponse pageLiveRoom(@RequestBody LiveRoomQueryRequest liveRoomQueryRequest) {
        Page<LiveRoomVO> page = liveRoomService.pageLiveRoom(liveRoomQueryRequest);
        return ApiResponse.success(page);
    }

    /**
     * 获取直播间列表
     */
    @PostMapping("/list")
    @ValidateRequest
    public ApiResponse listLiveRoom(@RequestBody LiveRoomQueryRequest liveRoomQueryRequest) {
        List<LiveRoomVO> list = liveRoomService.listLiveRoom(liveRoomQueryRequest);
        return ApiResponse.success(list);
    }

    // endregion

    // region 数据分析

    /**
     * 获取直播间数据分析
     */
    @GetMapping("/analysis")
    public ApiResponse getLiveDataAnalysis(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LiveDataAnalysisVO analysisVO = liveRoomService.getLiveDataAnalysis(liveRoomId);
        return ApiResponse.success(analysisVO);
    }

    // endregion
}

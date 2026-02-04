package com.rosy.web.controller.main;

import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.domain.entity.IdRequest;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.live.*;
import com.rosy.main.domain.vo.*;
import com.rosy.main.service.ILiveRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/live-room")
@Tag(name = "直播间管理")
public class LiveRoomController {

    @Resource
    private ILiveRoomService liveRoomService;

    @PostMapping("/create")
    @ValidateRequest
    @Operation(summary = "创建直播间")
    public ApiResponse createLiveRoom(@RequestBody LiveRoomCreateRequest request) {
        return ApiResponse.success(liveRoomService.createLiveRoom(request));
    }

    @PostMapping("/update")
    @ValidateRequest
    @Operation(summary = "更新直播间")
    public ApiResponse updateLiveRoom(@RequestBody LiveRoomUpdateRequest request) {
        if (request.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomService.updateLiveRoom(request));
    }

    @PostMapping("/update-status")
    @ValidateRequest
    @Operation(summary = "更新直播状态")
    public ApiResponse updateStatus(@RequestBody LiveRoomStatusRequest request) {
        if (request.getLiveRoomId() == null || request.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomService.updateStatus(request));
    }

    @PostMapping("/delete")
    @ValidateRequest
    @Operation(summary = "删除直播间")
    public ApiResponse deleteLiveRoom(@RequestBody IdRequest request) {
        return ApiResponse.success(liveRoomService.removeById(request.getId()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取直播间详情")
    public ApiResponse getLiveRoomDetail(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomService.getLiveRoomDetail(id));
    }

    @PostMapping("/list")
    @ValidateRequest
    @Operation(summary = "获取直播间列表")
    public ApiResponse getLiveRoomList(@RequestBody LiveRoomQueryRequest request) {
        return ApiResponse.success(liveRoomService.getLiveRoomList(request));
    }

    @GetMapping("/data")
    @Operation(summary = "获取直播间实时数据")
    public ApiResponse getLiveRoomData(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomService.getLiveRoomData(liveRoomId));
    }

    @GetMapping("/analytics")
    @Operation(summary = "获取直播间数据分析")
    public ApiResponse getLiveRoomAnalytics(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomService.getLiveRoomAnalytics(liveRoomId));
    }
}

package com.rosy.web.controller.main;

import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.main.domain.dto.liveRoomProduct.*;
import com.rosy.main.domain.vo.LiveRoomProductVO;
import com.rosy.main.service.ILiveRoomProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/live-room-product")
@Tag(name = "直播间商品关联管理")
public class LiveRoomProductController {

    @Resource
    private ILiveRoomProductService liveRoomProductService;

    @PostMapping("/add")
    @ValidateRequest
    @Operation(summary = "添加商品到直播间")
    public ApiResponse addProductsToLiveRoom(@RequestBody LiveRoomProductAddRequest request) {
        if (request.getLiveRoomId() == null || request.getProductIds() == null || request.getProductIds().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.addProductsToLiveRoom(request));
    }

    @PostMapping("/remove")
    @ValidateRequest
    @Operation(summary = "从直播间移除商品")
    public ApiResponse removeProductFromLiveRoom(@RequestParam Long liveRoomId, @RequestParam Long productId) {
        if (liveRoomId == null || productId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.removeProductFromLiveRoom(liveRoomId, productId));
    }

    @PostMapping("/sort")
    @ValidateRequest
    @Operation(summary = "更新商品排序")
    public ApiResponse updateProductSortOrder(@RequestBody LiveRoomProductSortRequest request) {
        if (request.getLiveRoomId() == null || request.getProductSorts() == null || request.getProductSorts().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.updateProductSortOrder(request));
    }

    @PostMapping("/switch-explaining")
    @Operation(summary = "切换讲解商品")
    public ApiResponse switchExplainingProduct(@RequestParam Long liveRoomId, @RequestParam Long productId) {
        if (liveRoomId == null || productId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.switchExplainingProduct(liveRoomId, productId));
    }

    @GetMapping("/list")
    @Operation(summary = "获取直播间商品列表")
    public ApiResponse getLiveRoomProducts(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.getLiveRoomProducts(liveRoomId));
    }

    @GetMapping("/explaining")
    @Operation(summary = "获取当前讲解商品")
    public ApiResponse<LiveRoomProductVO> getExplainingProduct(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResponse.success(liveRoomProductService.getExplainingProduct(liveRoomId));
    }
}

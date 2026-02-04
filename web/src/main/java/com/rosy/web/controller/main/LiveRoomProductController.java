package com.rosy.web.controller.main;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.domain.entity.IdRequest;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductQueryRequest;
import com.rosy.main.domain.dto.liveroom.LiveRoomProductUpdateRequest;
import com.rosy.main.domain.vo.LiveRoomProductVO;
import com.rosy.main.service.ILiveRoomProductService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 直播间商品管理 前端控制器
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/liveRoomProduct")
public class LiveRoomProductController {

    @Resource
    private ILiveRoomProductService liveRoomProductService;

    /**
     * 添加直播间商品
     */
    @PostMapping("/add")
    @ValidateRequest
    public ApiResponse addLiveRoomProduct(@RequestBody LiveRoomProductAddRequest liveRoomProductAddRequest) {
        Long id = liveRoomProductService.addLiveRoomProduct(liveRoomProductAddRequest);
        return ApiResponse.success(id);
    }

    /**
     * 删除直播间商品
     */
    @PostMapping("/delete")
    @ValidateRequest
    public ApiResponse deleteLiveRoomProduct(@RequestBody IdRequest idRequest) {
        boolean result = liveRoomProductService.deleteLiveRoomProduct(idRequest.getId());
        return ApiResponse.success(result);
    }

    /**
     * 更新直播间商品
     */
    @PostMapping("/update")
    @ValidateRequest
    public ApiResponse updateLiveRoomProduct(@RequestBody LiveRoomProductUpdateRequest liveRoomProductUpdateRequest) {
        boolean result = liveRoomProductService.updateLiveRoomProduct(liveRoomProductUpdateRequest);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取直播间商品
     */
    @GetMapping("/get")
    public ApiResponse getLiveRoomProductById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LiveRoomProductVO productVO = liveRoomProductService.getLiveRoomProductById(id);
        ThrowUtils.throwIf(productVO == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResponse.success(productVO);
    }

    /**
     * 根据直播间ID获取商品列表
     */
    @GetMapping("/listByLiveRoom")
    public ApiResponse getProductsByLiveRoomId(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<LiveRoomProductVO> list = liveRoomProductService.getProductsByLiveRoomId(liveRoomId);
        return ApiResponse.success(list);
    }

    /**
     * 分页查询直播间商品
     */
    @PostMapping("/list/page")
    @ValidateRequest
    public ApiResponse pageLiveRoomProduct(@RequestBody LiveRoomProductQueryRequest liveRoomProductQueryRequest) {
        Page<LiveRoomProductVO> page = liveRoomProductService.pageLiveRoomProduct(liveRoomProductQueryRequest);
        return ApiResponse.success(page);
    }

    /**
     * 获取商品排行榜
     */
    @GetMapping("/ranking")
    public ApiResponse getProductRanking(@RequestParam Long liveRoomId,
                                                                   @RequestParam(defaultValue = "10") int limit) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<LiveRoomProductVO> list = liveRoomProductService.getProductRanking(liveRoomId, limit);
        return ApiResponse.success(list);
    }
}

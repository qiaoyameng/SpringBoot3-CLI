package com.rosy.web.controller.main;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rosy.common.annotation.ValidateRequest;
import com.rosy.common.domain.entity.ApiResponse;
import com.rosy.common.domain.entity.IdRequest;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.LiveOrderAddRequest;
import com.rosy.main.domain.dto.liveroom.LiveOrderQueryRequest;
import com.rosy.main.domain.vo.LiveOrderVO;
import com.rosy.main.service.ILiveOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间订单管理 前端控制器
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/liveOrder")
public class LiveOrderController {

    @Resource
    private ILiveOrderService liveOrderService;

    /**
     * 创建订单
     */
    @PostMapping("/add")
    @ValidateRequest
    public ApiResponse addLiveOrder(@RequestBody LiveOrderAddRequest liveOrderAddRequest) {
        Long id = liveOrderService.addLiveOrder(liveOrderAddRequest);
        return ApiResponse.success(id);
    }

    /**
     * 删除订单
     */
    @PostMapping("/delete")
    @ValidateRequest
    public ApiResponse deleteLiveOrder(@RequestBody IdRequest idRequest) {
        boolean result = liveOrderService.deleteLiveOrder(idRequest.getId());
        return ApiResponse.success(result);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay")
    @ValidateRequest
    public ApiResponse payOrder(@RequestBody IdRequest idRequest) {
        boolean result = liveOrderService.payOrder(idRequest.getId());
        return ApiResponse.success(result);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    @ValidateRequest
    public ApiResponse cancelOrder(@RequestBody IdRequest idRequest) {
        boolean result = liveOrderService.cancelOrder(idRequest.getId());
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取订单
     */
    @GetMapping("/get")
    public ApiResponse getLiveOrderById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LiveOrderVO orderVO = liveOrderService.getLiveOrderById(id);
        ThrowUtils.throwIf(orderVO == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResponse.success(orderVO);
    }

    /**
     * 分页查询订单
     */
    @PostMapping("/list/page")
    @ValidateRequest
    public ApiResponse pageLiveOrder(@RequestBody LiveOrderQueryRequest liveOrderQueryRequest) {
        Page<LiveOrderVO> page = liveOrderService.pageLiveOrder(liveOrderQueryRequest);
        return ApiResponse.success(page);
    }

    /**
     * 获取直播间订单列表
     */
    @GetMapping("/listByLiveRoom")
    public ApiResponse getOrdersByLiveRoomId(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<LiveOrderVO> list = liveOrderService.getOrdersByLiveRoomId(liveRoomId);
        return ApiResponse.success(list);
    }

    /**
     * 获取直播间订单统计
     */
    @GetMapping("/stats")
    public ApiResponse getOrderStatsByLiveRoomId(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Object> stats = liveOrderService.getOrderStatsByLiveRoomId(liveRoomId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取直播间商品销售统计
     */
    @GetMapping("/productSalesStats")
    public ApiResponse getProductSalesStats(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Map<String, Object>> stats = liveOrderService.getProductSalesStats(liveRoomId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取直播间总销售额
     */
    @GetMapping("/totalSalesAmount")
    public ApiResponse getTotalSalesAmount(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BigDecimal amount = liveOrderService.getTotalSalesAmount(liveRoomId);
        return ApiResponse.success(amount);
    }

    /**
     * 获取直播间已支付订单数
     */
    @GetMapping("/paidOrderCount")
    public ApiResponse getPaidOrderCount(@RequestParam Long liveRoomId) {
        if (liveRoomId == null || liveRoomId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int count = liveOrderService.getPaidOrderCount(liveRoomId);
        return ApiResponse.success(count);
    }
}

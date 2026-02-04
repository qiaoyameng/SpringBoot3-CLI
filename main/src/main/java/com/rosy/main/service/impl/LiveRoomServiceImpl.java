package com.rosy.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rosy.common.enums.ErrorCode;
import com.rosy.common.exception.BusinessException;
import com.rosy.common.utils.QueryWrapperUtil;
import com.rosy.common.utils.ThrowUtils;
import com.rosy.main.domain.dto.liveroom.*;
import com.rosy.main.domain.entity.LiveRoom;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.vo.LiveDataAnalysisVO;
import com.rosy.main.domain.vo.LiveRoomProductVO;
import com.rosy.main.domain.vo.LiveRoomVO;
import com.rosy.main.mapper.LiveRoomMapper;
import com.rosy.main.service.ILiveOrderService;
import com.rosy.main.service.ILiveRoomProductService;
import com.rosy.main.service.ILiveRoomService;
import com.rosy.main.service.ILiveViewerService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 直播间表 服务实现类
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Service
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom> implements ILiveRoomService {

    @Resource
    private ILiveRoomProductService liveRoomProductService;

    @Resource
    private ILiveOrderService liveOrderService;

    @Resource
    private ILiveViewerService liveViewerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addLiveRoom(LiveRoomAddRequest liveRoomAddRequest) {
        LiveRoom liveRoom = new LiveRoom();
        BeanUtils.copyProperties(liveRoomAddRequest, liveRoom);
        liveRoom.setStatus((byte) 0);
        liveRoom.setViewerCount(0);
        liveRoom.setTotalViewerCount(0);
        liveRoom.setOrderCount(0);
        liveRoom.setSalesAmount(BigDecimal.ZERO);
        boolean result = this.save(liveRoom);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return liveRoom.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLiveRoom(Long id) {
        return this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLiveRoom(LiveRoomUpdateRequest liveRoomUpdateRequest) {
        LiveRoom liveRoom = new LiveRoom();
        BeanUtils.copyProperties(liveRoomUpdateRequest, liveRoom);
        boolean result = this.updateById(liveRoom);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateLiveRoomStatus(LiveRoomStatusUpdateRequest liveRoomStatusUpdateRequest) {
        Long id = liveRoomStatusUpdateRequest.getId();
        Byte status = liveRoomStatusUpdateRequest.getStatus();

        LiveRoom liveRoom = this.getById(id);
        ThrowUtils.throwIf(liveRoom == null, ErrorCode.NOT_FOUND_ERROR);

        LiveRoom updateRoom = new LiveRoom();
        updateRoom.setId(id);
        updateRoom.setStatus(status);

        // 根据状态更新开始/结束时间
        if (status == 1 && liveRoom.getStatus() != 1) {
            // 开始直播
            updateRoom.setStartTime(LocalDateTime.now());
        } else if (status == 2 && liveRoom.getStatus() != 2) {
            // 结束直播
            updateRoom.setEndTime(LocalDateTime.now());
        }

        boolean result = this.updateById(updateRoom);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean switchCurrentProduct(LiveRoomSwitchProductRequest liveRoomSwitchProductRequest) {
        Long liveRoomId = liveRoomSwitchProductRequest.getLiveRoomId();
        Long productId = liveRoomSwitchProductRequest.getProductId();

        // 验证直播间是否存在
        LiveRoom liveRoom = this.getById(liveRoomId);
        ThrowUtils.throwIf(liveRoom == null, ErrorCode.NOT_FOUND_ERROR, "直播间不存在");

        // 验证商品是否关联该直播间
        LiveRoomProductVO productVO = liveRoomProductService.getLiveRoomProductById(productId);
        ThrowUtils.throwIf(productVO == null, ErrorCode.NOT_FOUND_ERROR, "商品不存在");
        ThrowUtils.throwIf(!productVO.getLiveRoomId().equals(liveRoomId), ErrorCode.PARAMS_ERROR, "商品不属于该直播间");

        // 更新当前讲解商品
        baseMapper.updateCurrentProduct(liveRoomId, productId, null);
        return true;
    }

    @Override
    public LiveRoomVO getLiveRoomById(Long id) {
        LiveRoom liveRoom = this.getById(id);
        if (liveRoom == null) {
            return null;
        }
        return getLiveRoomVO(liveRoom);
    }

    @Override
    public Page<LiveRoomVO> pageLiveRoom(LiveRoomQueryRequest liveRoomQueryRequest) {
        long current = liveRoomQueryRequest.getCurrent();
        long size = liveRoomQueryRequest.getPageSize();
        LambdaQueryWrapper<LiveRoom> queryWrapper = getQueryWrapper(liveRoomQueryRequest);
        Page<LiveRoom> page = this.page(new Page<>(current, size), queryWrapper);
        List<LiveRoomVO> voList = getLiveRoomVOList(page.getRecords());
        Page<LiveRoomVO> voPage = new Page<>(current, size, page.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<LiveRoomVO> listLiveRoom(LiveRoomQueryRequest liveRoomQueryRequest) {
        LambdaQueryWrapper<LiveRoom> queryWrapper = getQueryWrapper(liveRoomQueryRequest);
        List<LiveRoom> list = this.list(queryWrapper);
        return getLiveRoomVOList(list);
    }

    @Override
    public LiveDataAnalysisVO getLiveDataAnalysis(Long liveRoomId) {
        LiveRoom liveRoom = this.getById(liveRoomId);
        ThrowUtils.throwIf(liveRoom == null, ErrorCode.NOT_FOUND_ERROR);

        LiveDataAnalysisVO analysisVO = new LiveDataAnalysisVO();
        analysisVO.setLiveRoomId(liveRoomId);
        analysisVO.setLiveRoomTitle(liveRoom.getTitle());

        // 观众数据
        Map<String, Object> viewerStats = liveViewerService.getViewerStats(liveRoomId);
        analysisVO.setTotalViewerCount(((Number) viewerStats.get("total_viewers")).intValue());
        analysisVO.setPeakViewerCount(liveViewerService.getPeakViewerCount(liveRoomId));
        analysisVO.setAvgViewDuration(liveViewerService.getAvgViewDuration(liveRoomId));

        // 销售数据
        analysisVO.setTotalOrderCount(liveOrderService.getPaidOrderCount(liveRoomId));
        analysisVO.setTotalSalesAmount(liveOrderService.getTotalSalesAmount(liveRoomId));

        // 转化率 = 订单数 / 总观看人数 * 100
        int totalViewers = analysisVO.getTotalViewerCount();
        int totalOrders = analysisVO.getTotalOrderCount();
        BigDecimal conversionRate = totalViewers > 0
                ? BigDecimal.valueOf(totalOrders).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalViewers), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        analysisVO.setConversionRate(conversionRate);

        // 商品排行榜
        List<LiveRoomProductVO> productRanking = liveRoomProductService.getProductRanking(liveRoomId, 10);
        List<Map<String, Object>> salesStats = liveOrderService.getProductSalesStats(liveRoomId);
        
        List<LiveDataAnalysisVO.ProductRankingVO> rankingVOList = new java.util.ArrayList<>();
        for (int i = 0; i < productRanking.size(); i++) {
            LiveRoomProductVO product = productRanking.get(i);
            LiveDataAnalysisVO.ProductRankingVO rankingVO = new LiveDataAnalysisVO.ProductRankingVO();
            rankingVO.setProductId(product.getProductId());
            rankingVO.setProductName(product.getProductName());
            rankingVO.setProductImage(product.getProductImage());
            rankingVO.setSalesCount(product.getSalesCount());
            // 查询商品销售额
            BigDecimal salesAmount = salesStats.stream()
                    .filter(stat -> stat.get("product_id").equals(product.getProductId()))
                    .map(stat -> (BigDecimal) stat.get("sales_amount"))
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
            rankingVO.setSalesAmount(salesAmount);
            rankingVO.setRank(i + 1);
            rankingVOList.add(rankingVO);
        }
        analysisVO.setProductRanking(rankingVOList);

        // 观众留存曲线
        analysisVO.setRetentionCurve(liveViewerService.getRetentionData(liveRoomId));

        return analysisVO;
    }

    @Override
    public LambdaQueryWrapper<LiveRoom> getQueryWrapper(LiveRoomQueryRequest liveRoomQueryRequest) {
        if (liveRoomQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        LambdaQueryWrapper<LiveRoom> queryWrapper = new LambdaQueryWrapper<>();

        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getId(), LiveRoom::getId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getTitle(), LiveRoom::getTitle);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getAnchorId(), LiveRoom::getAnchorId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getStatus(), LiveRoom::getStatus);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getCreatorId(), LiveRoom::getCreatorId);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getCreateTime(), LiveRoom::getCreateTime);
        QueryWrapperUtil.addCondition(queryWrapper, liveRoomQueryRequest.getUpdateTime(), LiveRoom::getUpdateTime);

        QueryWrapperUtil.addSortCondition(queryWrapper,
                liveRoomQueryRequest.getSortField(),
                liveRoomQueryRequest.getSortOrder(),
                LiveRoom::getId);

        return queryWrapper;
    }

    @Override
    public LiveRoomVO getLiveRoomVO(LiveRoom liveRoom) {
        if (liveRoom == null) {
            return null;
        }
        LiveRoomVO vo = new LiveRoomVO();
        BeanUtils.copyProperties(liveRoom, vo);

        // 设置状态文本
        vo.setStatusText(getStatusText(liveRoom.getStatus()));

        // 加载当前讲解商品
        if (liveRoom.getCurrentProductId() != null) {
            LiveRoomProductVO currentProduct = liveRoomProductService.getLiveRoomProductById(liveRoom.getCurrentProductId());
            vo.setCurrentProduct(currentProduct);
        }

        // 加载商品列表
        List<LiveRoomProductVO> products = liveRoomProductService.getProductsByLiveRoomId(liveRoom.getId());
        vo.setProducts(products);

        return vo;
    }

    @Override
    public List<LiveRoomVO> getLiveRoomVOList(List<LiveRoom> liveRoomList) {
        return liveRoomList.stream()
                .map(this::getLiveRoomVO)
                .collect(Collectors.toList());
    }

    private String getStatusText(Byte status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "未开始";
            case 1 -> "直播中";
            case 2 -> "已结束";
            case 3 -> "暂停";
            default -> "未知";
        };
    }
}

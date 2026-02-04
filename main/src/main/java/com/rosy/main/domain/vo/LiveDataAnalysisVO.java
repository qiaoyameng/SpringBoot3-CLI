package com.rosy.main.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 直播间数据分析VO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveDataAnalysisVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    private Long liveRoomId;

    /**
     * 直播间标题
     */
    private String liveRoomTitle;

    /**
     * 总观看人数
     */
    private Integer totalViewerCount;

    /**
     * 峰值观看人数
     */
    private Integer peakViewerCount;

    /**
     * 平均观看时长（秒）
     */
    private Integer avgViewDuration;

    /**
     * 总订单数
     */
    private Integer totalOrderCount;

    /**
     * 总销售额
     */
    private BigDecimal totalSalesAmount;

    /**
     * 直播转化率（%）
     */
    private BigDecimal conversionRate;

    /**
     * 商品排行榜
     */
    private List<ProductRankingVO> productRanking;

    /**
     * 观众留存曲线数据
     */
    private List<ViewerRetentionVO> retentionCurve;

    /**
     * 每小时统计数据
     */
    private List<LiveRoomStatsVO> hourlyStats;

    @Data
    public static class ProductRankingVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品图片
         */
        private String productImage;

        /**
         * 销量
         */
        private Integer salesCount;

        /**
         * 销售额
         */
        private BigDecimal salesAmount;

        /**
         * 排名
         */
        private Integer rank;
    }

    @Data
    public static class ViewerRetentionVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 时间节点（分钟）
         */
        private Integer timePoint;

        /**
         * 留存人数
         */
        private Integer retentionCount;

        /**
         * 留存率（%）
         */
        private BigDecimal retentionRate;
    }
}

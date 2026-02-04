package com.rosy.main.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 直播间数据统计VO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomStatsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 直播间ID
     */
    private Long liveRoomId;

    /**
     * 统计小时
     */
    private LocalDateTime statsHour;

    /**
     * 观看人数
     */
    private Integer viewerCount;

    /**
     * 新增观看人数
     */
    private Integer newViewerCount;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 销售额
     */
    private BigDecimal salesAmount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

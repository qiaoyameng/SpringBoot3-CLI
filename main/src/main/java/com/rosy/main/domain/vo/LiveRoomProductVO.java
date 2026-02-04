package com.rosy.main.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 直播间商品VO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomProductVO implements Serializable {

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
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片URL
     */
    private String productImage;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 商品卖点列表
     */
    private List<String> sellingPoints;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    private Byte status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 销量
     */
    private Integer salesCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

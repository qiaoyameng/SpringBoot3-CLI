package com.rosy.main.domain.dto.liveroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 直播间商品添加请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomProductAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    @NotNull(message = "直播间ID不能为空")
    private Long liveRoomId;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String productName;

    /**
     * 商品图片URL
     */
    private String productImage;

    /**
     * 商品价格
     */
    @NotNull(message = "商品价格不能为空")
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
}

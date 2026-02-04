package com.rosy.main.domain.dto.liveroom;

import com.rosy.common.domain.entity.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 直播间商品查询请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LiveRoomProductQueryRequest extends PageRequest implements Serializable {

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
     * 状态：0-下架，1-上架
     */
    private Byte status;
}

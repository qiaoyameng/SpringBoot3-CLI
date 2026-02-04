package com.rosy.main.domain.dto.liveroom;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 直播间更新请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    @NotNull(message = "直播间ID不能为空")
    private Long id;

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 直播间简介
     */
    private String description;

    /**
     * 直播间封面图URL
     */
    private String coverImage;

    /**
     * 主播名称
     */
    private String anchorName;

    /**
     * 直播状态：0-未开始，1-直播中，2-已结束，3-暂停
     */
    private Byte status;

    /**
     * 预计直播开始时间
     */
    private LocalDateTime startTime;

    /**
     * 当前讲解商品ID
     */
    private Long currentProductId;
}

package com.rosy.main.domain.dto.liveroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 直播间创建请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间标题
     */
    @NotBlank(message = "直播间标题不能为空")
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
     * 主播ID
     */
    @NotNull(message = "主播ID不能为空")
    private Long anchorId;

    /**
     * 主播名称
     */
    private String anchorName;

    /**
     * 预计直播开始时间
     */
    private LocalDateTime startTime;
}

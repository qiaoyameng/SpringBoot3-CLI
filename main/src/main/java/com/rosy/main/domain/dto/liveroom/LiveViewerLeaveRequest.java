package com.rosy.main.domain.dto.liveroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 观众离开直播间请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveViewerLeaveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    @NotNull(message = "直播间ID不能为空")
    private Long liveRoomId;

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}

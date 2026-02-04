package com.rosy.main.domain.dto.liveroom;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 直播间状态更新请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
public class LiveRoomStatusUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    @NotNull(message = "直播间ID不能为空")
    private Long id;

    /**
     * 直播状态：0-未开始，1-直播中，2-已结束，3-暂停
     */
    @NotNull(message = "直播状态不能为空")
    private Byte status;
}

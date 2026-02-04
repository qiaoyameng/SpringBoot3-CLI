package com.rosy.main.domain.dto.liveroom;

import com.rosy.common.domain.entity.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 直播间查询请求DTO
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LiveRoomQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 直播间ID
     */
    private Long id;

    /**
     * 直播间标题
     */
    private String title;

    /**
     * 主播ID
     */
    private Long anchorId;

    /**
     * 直播状态：0-未开始，1-直播中，2-已结束，3-暂停
     */
    private Byte status;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

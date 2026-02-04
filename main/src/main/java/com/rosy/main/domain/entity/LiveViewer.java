package com.rosy.main.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 直播间观众记录表
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
@Data
@TableName("live_viewer")
public class LiveViewer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 直播间ID
     */
    private Long liveRoomId;

    /**
     * 用户ID（未登录为null）
     */
    private Long userId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 进入时间
     */
    private LocalDateTime enterTime;

    /**
     * 离开时间
     */
    private LocalDateTime leaveTime;

    /**
     * 观看时长（秒）
     */
    private Integer duration;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建者ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creatorId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updaterId;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Byte version;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Byte isDeleted;
}

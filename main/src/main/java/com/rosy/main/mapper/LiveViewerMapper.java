package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveViewer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间观众记录表 Mapper 接口
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface LiveViewerMapper extends BaseMapper<LiveViewer> {

    /**
     * 根据直播间ID和会话ID查询观众记录
     */
    @Select("SELECT * FROM live_viewer WHERE live_room_id = #{liveRoomId} AND session_id = #{sessionId} AND is_deleted = 0 ORDER BY create_time DESC LIMIT 1")
    LiveViewer selectByLiveRoomIdAndSessionId(@Param("liveRoomId") Long liveRoomId, @Param("sessionId") String sessionId);

    /**
     * 更新观众离开信息
     */
    @Update("UPDATE live_viewer SET leave_time = #{leaveTime}, duration = #{duration}, updater_id = #{updaterId}, update_time = NOW() WHERE id = #{id}")
    int updateLeaveInfo(@Param("id") Long id, @Param("leaveTime") LocalDateTime leaveTime, @Param("duration") int duration, @Param("updaterId") Long updaterId);

    /**
     * 统计直播间观众数据
     */
    @Select("SELECT COUNT(*) as total_viewers, COALESCE(AVG(duration), 0) as avg_duration FROM live_viewer WHERE live_room_id = #{liveRoomId} AND is_deleted = 0")
    Map<String, Object> selectStatsByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    /**
     * 统计直播间峰值观看人数
     */
    @Select("SELECT COUNT(*) FROM live_viewer WHERE live_room_id = #{liveRoomId} AND enter_time <= #{timePoint} AND (leave_time IS NULL OR leave_time > #{timePoint}) AND is_deleted = 0")
    int countViewersAtTimePoint(@Param("liveRoomId") Long liveRoomId, @Param("timePoint") LocalDateTime timePoint);

    /**
     * 按时间段统计观众留存数据
     */
    @Select("SELECT FLOOR(duration / 60) as time_point, COUNT(*) as retention_count FROM live_viewer WHERE live_room_id = #{liveRoomId} AND duration IS NOT NULL AND is_deleted = 0 GROUP BY FLOOR(duration / 60) ORDER BY time_point")
    List<Map<String, Object>> selectRetentionData(@Param("liveRoomId") Long liveRoomId);

    /**
     * 统计直播间独立访客数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM live_viewer WHERE live_room_id = #{liveRoomId} AND user_id IS NOT NULL AND is_deleted = 0")
    int countUniqueViewers(@Param("liveRoomId") Long liveRoomId);
}

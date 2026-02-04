package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveRoomStats;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 直播间数据统计表 Mapper 接口
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface LiveRoomStatsMapper extends BaseMapper<LiveRoomStats> {

    /**
     * 根据直播间ID查询统计数据
     */
    @Select("SELECT * FROM live_room_stats WHERE live_room_id = #{liveRoomId} AND is_deleted = 0 ORDER BY stats_hour ASC")
    List<LiveRoomStats> selectByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    /**
     * 查询直播间在指定时间范围内的统计数据
     */
    @Select("SELECT * FROM live_room_stats WHERE live_room_id = #{liveRoomId} AND stats_hour >= #{startTime} AND stats_hour <= #{endTime} AND is_deleted = 0 ORDER BY stats_hour ASC")
    List<LiveRoomStats> selectByTimeRange(@Param("liveRoomId") Long liveRoomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询直播间最大观看人数
     */
    @Select("SELECT COALESCE(MAX(viewer_count), 0) FROM live_room_stats WHERE live_room_id = #{liveRoomId} AND is_deleted = 0")
    int selectMaxViewerCount(@Param("liveRoomId") Long liveRoomId);
}

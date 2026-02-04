package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.ViewersStatsMinute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ViewersStatsMinuteMapper extends BaseMapper<ViewersStatsMinute> {

    List<ViewersStatsMinute> selectByLiveRoomId(@Param("liveRoomId") Long liveRoomId);
}

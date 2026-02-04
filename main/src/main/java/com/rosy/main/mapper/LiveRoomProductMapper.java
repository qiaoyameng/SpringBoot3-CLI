package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveRoomProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveRoomProductMapper extends BaseMapper<LiveRoomProduct> {

    List<LiveRoomProduct> selectByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    LiveRoomProduct selectExplainingProduct(@Param("liveRoomId") Long liveRoomId);
}

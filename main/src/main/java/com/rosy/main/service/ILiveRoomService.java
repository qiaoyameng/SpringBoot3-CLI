package com.rosy.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.live.*;
import com.rosy.main.domain.entity.LiveRoom;
import com.rosy.main.domain.vo.*;

import java.util.List;

public interface ILiveRoomService extends IService<LiveRoom> {

    LiveRoomVO createLiveRoom(LiveRoomCreateRequest request);

    LiveRoomVO updateLiveRoom(LiveRoomUpdateRequest request);

    boolean updateStatus(LiveRoomStatusRequest request);

    LiveRoomVO getLiveRoomDetail(Long id);

    PageResult<LiveRoomVO> getLiveRoomList(LiveRoomQueryRequest request);

    LiveRoomDataVO getLiveRoomData(Long liveRoomId);

    LiveRoomAnalyticsVO getLiveRoomAnalytics(Long liveRoomId);
}

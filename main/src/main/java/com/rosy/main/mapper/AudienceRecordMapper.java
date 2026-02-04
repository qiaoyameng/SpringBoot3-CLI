package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.AudienceRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AudienceRecordMapper extends BaseMapper<AudienceRecord> {

    List<Map<String, Object>> selectRetentionData(@Param("liveRoomId") Long liveRoomId);

    Double selectConversionRate(@Param("liveRoomId") Long liveRoomId);

    Integer selectTotalAudiences(@Param("liveRoomId") Long liveRoomId);
}

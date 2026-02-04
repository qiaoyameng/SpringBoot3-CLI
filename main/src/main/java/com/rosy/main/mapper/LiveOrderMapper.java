package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface LiveOrderMapper extends BaseMapper<LiveOrder> {

    List<Map<String, Object>> selectProductSalesRanking(@Param("liveRoomId") Long liveRoomId);

    BigDecimal selectTotalSalesByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    Integer selectTotalOrdersByLiveRoomId(@Param("liveRoomId") Long liveRoomId);
}

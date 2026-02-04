package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 直播间订单表 Mapper 接口
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface LiveOrderMapper extends BaseMapper<LiveOrder> {

    /**
     * 统计直播间订单数据
     */
    @Select("SELECT COUNT(*) as order_count, COALESCE(SUM(total_amount), 0) as sales_amount FROM live_order WHERE live_room_id = #{liveRoomId} AND status = 1 AND is_deleted = 0")
    Map<String, Object> selectStatsByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    /**
     * 统计直播间各商品销售数据
     */
    @Select("SELECT product_id, product_name, COUNT(*) as sales_count, SUM(total_amount) as sales_amount FROM live_order WHERE live_room_id = #{liveRoomId} AND status = 1 AND is_deleted = 0 GROUP BY product_id, product_name ORDER BY sales_count DESC")
    List<Map<String, Object>> selectProductSalesStats(@Param("liveRoomId") Long liveRoomId);

    /**
     * 查询直播间已支付订单数
     */
    @Select("SELECT COUNT(*) FROM live_order WHERE live_room_id = #{liveRoomId} AND status = 1 AND is_deleted = 0")
    int selectPaidOrderCount(@Param("liveRoomId") Long liveRoomId);

    /**
     * 查询直播间总销售额
     */
    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM live_order WHERE live_room_id = #{liveRoomId} AND status = 1 AND is_deleted = 0")
    BigDecimal selectTotalSalesAmount(@Param("liveRoomId") Long liveRoomId);
}

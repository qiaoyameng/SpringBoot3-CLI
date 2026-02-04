package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveRoom;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 直播间表 Mapper 接口
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface LiveRoomMapper extends BaseMapper<LiveRoom> {

    /**
     * 更新直播间当前讲解商品
     */
    @Update("UPDATE live_room SET current_product_id = #{productId}, updater_id = #{updaterId}, update_time = NOW() WHERE id = #{liveRoomId}")
    int updateCurrentProduct(@Param("liveRoomId") Long liveRoomId, @Param("productId") Long productId, @Param("updaterId") Long updaterId);

    /**
     * 增加直播间观看人数
     */
    @Update("UPDATE live_room SET viewer_count = viewer_count + #{count}, total_viewer_count = total_viewer_count + #{count}, updater_id = #{updaterId}, update_time = NOW() WHERE id = #{liveRoomId}")
    int increaseViewerCount(@Param("liveRoomId") Long liveRoomId, @Param("count") int count, @Param("updaterId") Long updaterId);

    /**
     * 减少直播间观看人数
     */
    @Update("UPDATE live_room SET viewer_count = GREATEST(0, viewer_count - #{count}), updater_id = #{updaterId}, update_time = NOW() WHERE id = #{liveRoomId}")
    int decreaseViewerCount(@Param("liveRoomId") Long liveRoomId, @Param("count") int count, @Param("updaterId") Long updaterId);

    /**
     * 更新直播间销售数据
     */
    @Update("UPDATE live_room SET order_count = order_count + #{orderCount}, sales_amount = sales_amount + #{salesAmount}, updater_id = #{updaterId}, update_time = NOW() WHERE id = #{liveRoomId}")
    int updateSalesData(@Param("liveRoomId") Long liveRoomId, @Param("orderCount") int orderCount, @Param("salesAmount") java.math.BigDecimal salesAmount, @Param("updaterId") Long updaterId);
}

package com.rosy.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rosy.main.domain.entity.LiveRoomProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 直播间商品关联表 Mapper 接口
 * </p>
 *
 * @author Rosy
 * @since 2025-01-19
 */
public interface LiveRoomProductMapper extends BaseMapper<LiveRoomProduct> {

    /**
     * 根据直播间ID查询商品列表
     */
    @Select("SELECT * FROM live_room_product WHERE live_room_id = #{liveRoomId} AND is_deleted = 0 ORDER BY sort_order ASC, create_time DESC")
    List<LiveRoomProduct> selectByLiveRoomId(@Param("liveRoomId") Long liveRoomId);

    /**
     * 增加商品销量
     */
    @Update("UPDATE live_room_product SET sales_count = sales_count + #{count}, stock = GREATEST(0, stock - #{count}), updater_id = #{updaterId}, update_time = NOW() WHERE id = #{productId}")
    int increaseSalesCount(@Param("productId") Long productId, @Param("count") int count, @Param("updaterId") Long updaterId);

    /**
     * 查询直播间商品排行榜
     */
    @Select("SELECT * FROM live_room_product WHERE live_room_id = #{liveRoomId} AND is_deleted = 0 ORDER BY sales_count DESC LIMIT #{limit}")
    List<LiveRoomProduct> selectProductRanking(@Param("liveRoomId") Long liveRoomId, @Param("limit") int limit);
}

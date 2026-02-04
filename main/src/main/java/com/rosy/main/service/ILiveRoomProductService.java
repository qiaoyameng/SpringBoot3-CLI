package com.rosy.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rosy.main.domain.dto.liveRoomProduct.*;
import com.rosy.main.domain.entity.LiveRoomProduct;
import com.rosy.main.domain.vo.*;

import java.util.List;

public interface ILiveRoomProductService extends IService<LiveRoomProduct> {

    boolean addProductsToLiveRoom(LiveRoomProductAddRequest request);

    boolean removeProductFromLiveRoom(Long liveRoomId, Long productId);

    boolean updateProductSortOrder(LiveRoomProductSortRequest request);

    boolean switchExplainingProduct(Long liveRoomId, Long productId);

    List<LiveRoomProductVO> getLiveRoomProducts(Long liveRoomId);

    LiveRoomProductVO getExplainingProduct(Long liveRoomId);
}

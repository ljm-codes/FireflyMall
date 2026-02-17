package com.fm.Mapper;

import POJO.Order.OrderInfo;
import POJO.Order.OrderItem;
import POJO.commodity.goods;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void createOrder(OrderInfo orderInfo);

    List<goods> getGoods(List<Long> goodsIds);

    List<OrderItem> getOrderItem(Long orderId, Long userId);

    List<OrderInfo> getOrderRoughInfoList(Long userId);

    void updateOrderStatus(Long orderId, int i);

    void addOldOrderItem(Long orderId, Long userId, List<Map<String,Object>> items);

    void confirmOrderStatus(Long orderId, LocalDateTime now);

    LocalDateTime getPayTime(Long orderId);
}

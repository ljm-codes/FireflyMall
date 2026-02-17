package com.fm.Service;

import POJO.Order.OrderInfo;
import POJO.commodity.goods;
import POJO.DataList;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Map<String, Object> createOrder(OrderInfo orderInfo, Long userId);

    DataList<goods> getOrder(Long orderId);

    List<OrderInfo> getOrderRoughInfoList();

    void cancelOrderStatus(Long orderId);

    void confirmOrderStatus(Long orderId);
}

package com.fm.Service;

import POJO.Order.OrderItem;

import java.util.Map;

public interface PayService {

    String createPay(OrderItem orderItem);

    Map<String, Object> queryOrderStatus(String orderId);
}

package com.fm.Controller;

import POJO.Login.UserContext;
import POJO.Login.UserInfo;
import POJO.Order.OrderInfo;
import POJO.Result;
import POJO.commodity.goods;
import POJO.DataList;
import com.fm.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class Order {

    private final OrderService orderService;

    @PostMapping("/create")
    public Result createOrder(@RequestBody OrderInfo orderInfo) {
        log.info("创建订单");
        log.info("orderInfo: {}", orderInfo);
        if (orderInfo.getReceiptAddress() == null || orderInfo.getReceiptAddress().isEmpty()) {
            return Result.error(HttpStatus.BAD_REQUEST, "请输入发货地址");
        }
        UserInfo userInfo = UserContext.getUserInfo();
        Long userId = userInfo.getId();
        Map<String, Object> orderItemMap;
        try {
            orderItemMap = orderService.createOrder(orderInfo, userId);
        } catch (Exception e) {
            log.error("创建订单失败：{}", e.getMessage());
            return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return Result.success(orderItemMap);
    }

    @GetMapping("/get/{orderId}")
    public Result getOrder(@PathVariable Long orderId) {
        log.info("获取订单");
        log.info("orderId: {}", orderId);
        try {
            DataList<goods> orderInfo = orderService.getOrder(orderId);
            return Result.success(orderInfo);
        } catch (Exception e) {
            log.error("获取订单失败：{}", e.getMessage());
            return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/get")
    public Result getOrderRoughInfoList() {
        log.info("获取订单粗略信息列表");
        try {
            List<OrderInfo> orderInfoList = orderService.getOrderRoughInfoList();
            return Result.success(orderInfoList);
        } catch (Exception e) {
            log.error("获取订单粗略信息列表失败：{}", e.getMessage());
            return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    public Result cancelOrderStatus(@PathVariable Long orderId) {
        log.info("取消订单");
        try {
            orderService.cancelOrderStatus(orderId);
            return Result.success();
        } catch (Exception e) {
            log.error("取消订单失败：{}", e.getMessage());
            return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("{orderId}/confirm")
    public Result confirmOrderStatus(@PathVariable Long orderId) {
        log.info("确认收货");
        try {
            orderService.confirmOrderStatus(orderId);
            return Result.success();
        } catch (Exception e) {
            log.error("确认收货失败：{}", e.getMessage());
            return Result.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}

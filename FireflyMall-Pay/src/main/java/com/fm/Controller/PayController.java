package com.fm.Controller;

import POJO.Order.OrderItem;
import POJO.Result;
import com.fm.Service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/pay/Alipay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @PostMapping("/create")
    public Result createPay(@RequestBody OrderItem orderItem) {
        if (orderItem.getOrderId() == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "订单ID不能为空");
        }
        try {
            String Base64PayUrl = payService.createPay(orderItem);
            return Result.success(Base64PayUrl);
        } catch (Exception e) {
            return Result.error(HttpStatus.NOT_FOUND, "创建支付失败" + e.getMessage());
        }
    }

    @GetMapping("/getStatus/{orderId}")
    public Result getStatus(@PathVariable String orderId) {
        log.info("获取支付状态,订单ID:{}", orderId);
        try {
            Map<String, Object> queryAlipayOrderStatusByOrderId = payService.queryOrderStatus(orderId);
            log.info("获取支付状态,订单ID:{}", orderId);
            return Result.success(queryAlipayOrderStatusByOrderId);
        } catch (Exception e) {
            return Result.error(HttpStatus.NOT_FOUND, "获取支付状态失败" + e.getMessage());
        }
    }

}

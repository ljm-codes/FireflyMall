package com.fm.Service.Impl;

import POJO.Order.OrderItem;
import Tools.JWT.Image.createQRCodePay;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.fm.Mapper.PayMapper;
import com.fm.Service.PayService;
import com.fm.configuration.AlipayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final PayMapper payMapper;

    @Override
    public String createPay(OrderItem orderItem) {
        log.info("创建支付订单，订单ID：{}", orderItem.getOrderId());
        String orderId = orderItem.getOrderId().toString();
        log.info("解析的订单ID：{}", orderId);
        String orderData = (String) redisTemplate.opsForValue().get(orderId);
        log.info("从Redis中获取到的订单数据：{}", orderData);
        if(orderData == null) {
            log.error("订单不存在，可能是超时了");
            throw new RuntimeException("订单不存在，可能是超时了");
        }
        try {
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setReturnUrl(alipayConfig.getRETURN_URL());

            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(orderItem.getOrderId().toString());

            Map<String, Object> map = JSONUtil.toBean(orderData, Map.class);

            float totalAmount = (Integer) map.get("totalAmount") / 100.0f;

            model.setTotalAmount(String.format("%.2f", totalAmount));

            model.setSubject("测试支付");

//            model.setProductCode("");

            model.setTimeoutExpress("30m");

            request.setBizModel(model);

            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            String qrCode = response.getQrCode();
            log.info("支付链接：{}", qrCode);
            String Base64Pay = createQRCodePay.generateQRCodeBase64(qrCode, 500, 500);
            log.info("二维码Base64编码：{}", Base64Pay);
            return Base64Pay;
        }catch (Exception e) {
            log.error("创建支付订单失败，原因为：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> queryOrderStatus(String orderId) {
        Map<String, Object> result = new HashMap<>();
        try{
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderId);
            request.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if(!response.isSuccess()) {
                throw new RuntimeException("查询支付订单状态失败: " + response.getSubMsg());
            }
            String tradeStatus = response.getTradeStatus();
            switch (tradeStatus) {
                case "TRADE_SUCCESS", "TRADE_FINISHED" -> {
                    LocalDateTime payTime = LocalDateTime.now();
                    payMapper.updateOrderStatus(orderId, 1, payTime);
                    result.put("status", "PAID");
                    result.put("orderId", orderId);
                    result.put("payTime", payTime);
                    result.put("message", "支付成功");
                }
                case "WAIT_BUYER_PAY" -> {
                    result.put("status", "WAIT");
                    result.put("orderId", orderId);
                    result.put("message", "支付等待中....");
                }
                case "TRADE_CLOSED" -> {
                    result.put("status", "CLOSED");
                    result.put("orderId", orderId);
                    result.put("message", "支付取消");
                }
                case null, default -> {
                    result.put("status", "UNKNOWN");
                    result.put("orderId", orderId);
                    result.put("message", "查询失败: " + response.getSubMsg());
                }
            }

            return result;
        }catch (Exception e){
            log.error("查询支付订单状态失败，原因为：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}

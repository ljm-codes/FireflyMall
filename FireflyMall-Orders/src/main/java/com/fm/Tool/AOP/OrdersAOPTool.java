package com.fm.Tool.AOP;

import POJO.Login.UserContext;
import POJO.ODS.CommodityRankData;
import POJO.Result;
import POJO.ToKafkaDataList;
import POJO.commodity.goods;
import Tools.KafkaTool.DataProcess;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.fm.Mapper.OrderMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrdersAOPTool {

    private final OrderMapper orderMapper;

    private final DataProcess<String, Object> dataProcess;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // java21+ 虚拟线程创建
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @AfterReturning(pointcut = "execution(* com.fm.Controller.Order.createOrder(..))", returning = "result")
    public void afterCreateOrder(JoinPoint joinPoint, Result result) {
        try {
            if (result.getCode() != 200) {
                log.error("创建订单失败，原因为：{}", result.getMsg());
                return;
            }
            String userName = UserContext.getUserInfo().getUsername();
            String userIP = UserContext.getUserInfo().getUserIP();
            // 从result中提取 Data
            Map<String, Object> data = Convert.convert(new TypeReference<>() {
            }, result.getData());
            Long orderId = (Long) data.get("orderId");
            List<Long> goodsIds = Convert.convert(new TypeReference<>() {
            }, data.get("goodsIds"));
            List<Integer> quantities = Convert.convert(new TypeReference<>() {
            }, data.get("quantities"));

            if (goodsIds.size() != quantities.size()) {
                log.error("创建订单失败，商品数量与数量不匹配");
                return;
            }
            Map<Long, Integer> idAndQuantiyMap = new HashMap<>();
            for (int i = 0; i < goodsIds.size(); i++) {
                idAndQuantiyMap.put(goodsIds.get(i), quantities.get(i));
            }

            executor.execute(() -> sendOrderDataToKafka(userName, userIP, orderId, idAndQuantiyMap));
        } catch (Exception e) {
            log.error("AOP拦截createOrder后发送Kafka数据异常: {}", e.getMessage());
        }
    }

    @AfterReturning(pointcut = "execution(* com.fm.Controller.Order.cancelOrderStatus(..))", returning = "result")
    public void afterCancelOrderStatus(JoinPoint joinPoint, Result result) {
        try {
            if (result.getCode() != 200) {
                log.error("取消订单失败，原因为：{}", result.getMsg());
                return;
            }
            Long orderId = (Long) joinPoint.getArgs()[0];
            String orderIdStr = orderId.toString();
            String userName = UserContext.getUserInfo().getUsername();
            String userIP = UserContext.getUserInfo().getUserIP();
            executor.execute(() -> sendCancelOrderDataToKafka(userName, userIP, orderIdStr));
        } catch (Exception e) {
            log.error("AOP拦截cancelOrderStatus后发送Kafka数据异常: {}", e.getMessage());
        }
    }

    private void sendCancelOrderDataToKafka(String userName, String userIP, String orderIdStr) {
        ToKafkaDataList toKafkaDataList = new ToKafkaDataList();
        toKafkaDataList.setType("order");
        toKafkaDataList.setOrderId(orderIdStr);
        toKafkaDataList.setUsername(userName);
        toKafkaDataList.setUserIP(userIP);
        toKafkaDataList.setMessage("取消订单");
        toKafkaDataList.setTime(LocalDateTime.now());
        dataProcess.sendDataToKafka(kafkaTemplate, orderIdStr,toKafkaDataList, "GoodsRealTime");
    }

    private void sendOrderDataToKafka(String userName, String userIP, Long orderId, Map<Long, Integer> idAndQuantiyMap) {
        ToKafkaDataList orderData = new ToKafkaDataList();
        orderData.setType("order");
        orderData.setUsername(userName);
        orderData.setUserIP(userIP);
        orderData.setTime(LocalDateTime.now());
        orderData.setOrderId(orderId.toString());
        orderData.setMessage("创建订单");
        List<Long> goodsIds = idAndQuantiyMap.keySet().stream().toList();
        List<goods> goodsDataList = orderMapper.getGoods(goodsIds);
        List<CommodityRankData> filterOrderDataList = goodsDataList.stream()
                .map(data -> new CommodityRankData(
                        data.getId(),
                        idAndQuantiyMap.getOrDefault(data.getId(), 0),
                        data.getPrice(),
                        data.getImage(),
                        data.getCategory(),
                        data.getBrand(),
                        data.getSpec(),
                        data.getSold(),
                        data.getIsAD()
                ))
                .toList();
        orderData.setData(filterOrderDataList);
        dataProcess.sendDataToKafka(kafkaTemplate, userIP, orderData, "GoodsRealTime");

    }

    @PreDestroy
    public void closeExecutor() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("虚拟线程未完全终止，等待时间为5秒");
                executor.shutdownNow(); // 强制终止所有任务
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            executor.shutdownNow();
            log.error("虚拟线程终止时，出现错误：{}，已强制终止所有任务", e.getMessage());
        }

    }

}

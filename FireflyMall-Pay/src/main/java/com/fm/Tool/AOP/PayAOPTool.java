package com.fm.Tool.AOP;

import POJO.Login.UserContext;
import POJO.Result;
import POJO.ToKafkaDataList;
import Tools.KafkaTool.DataProcess;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PayAOPTool {

    private final DataProcess<String, Object> dataProcess;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @AfterReturning(pointcut = "execution(* com.fm.Controller.PayController.getStatus(..))", returning = "result")
    public void afterPayStatus(JoinPoint joinPoint, Result result) {
        try {
            if (result.getCode() != 200) {
                log.error("获取支付状态失败: {}", result.getMsg());
                return;
            }
            String orderId = (String) joinPoint.getArgs()[0];
            String userName = UserContext.getUserInfo().getUsername();
            String userIP = UserContext.getUserInfo().getUserIP();
            Map<String, Object> data = Convert.convert(new TypeReference<>() {
            }, result.getData());
            String message = (String) data.get("message");
            LocalDateTime payTime = (LocalDateTime) data.get("payTime");
            executor.execute(() -> sendPayStatusToKafka(orderId, userName, userIP, message, payTime));
        } catch (Exception e) {
            log.error("AOP拦截getStatus后发送Kafka数据异常: {}", e.getMessage());
        }
    }

    private void sendPayStatusToKafka(String orderId, String userName, String userIP, String message, LocalDateTime payTime) {
        ToKafkaDataList payStatusData = new ToKafkaDataList();
        payStatusData.setType("payStatus");
        payStatusData.setOrderId(orderId);
        payStatusData.setUsername(userName);
        payStatusData.setUserIP(userIP);
        payStatusData.setMessage(message);
        payStatusData.setTime(payTime);
        dataProcess.sendDataToKafka(kafkaTemplate, orderId, payStatusData, "GoodsRealTime");
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

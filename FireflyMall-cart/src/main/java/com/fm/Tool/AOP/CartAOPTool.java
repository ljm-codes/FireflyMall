package com.fm.Tool.AOP;

import POJO.Cart.shoppingCart;
import POJO.Login.UserContext;
import POJO.ODS.CommodityRankData;
import POJO.Result;
import POJO.ToKafkaDataList;
import Tools.KafkaTool.DataProcess;
import com.fm.Mapper.CartMapping;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CartAOPTool {

    private final DataProcess<String, Object> dataProcess;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CartMapping cartMapping;

    // java21+ 虚拟线程创建
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @AfterReturning(pointcut = "execution(* com.fm.Controller.Cart.addToCart(..)) ||" +
            "execution(* com.fm.Controller.Cart.updateCart(..)) ||" +
            "execution(* com.fm.Controller.Cart.deleteCart(..))",
            returning = "result")
    public void afterAddToCart(JoinPoint joinPoint, Result result) {
        try {
            if (result.getCode() != 200) {
                log.error("添加到购物车失败");
            }
            //  提取方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String methodName = methodSignature.getMethod().getName();
            String message = "";
            switch (methodName) {
                case "addCart" -> message = "add";
                case "updateCart" -> message = "update";
                case "deleteCart" -> message = "delete";
            }

            shoppingCart cart = (shoppingCart) joinPoint.getArgs()[0];
            Long id = cart.getId();
            Integer quantity = cart.getQuantity();
            String ip = UserContext.getUserInfo().getUserIP();
            String username = UserContext.getUserInfo().getUsername();
            String finalMessage = message;
            executor.execute(() -> sendCartDataToKafka(id, quantity, ip, username, finalMessage));
        } catch (Exception e) {
            log.error("AOP拦截addToCart后发送Kafka数据异常: {}", e.getMessage());
        }
    }

    private void sendCartDataToKafka(Long id, Integer quantity, String ip, String username, String message) {
        ToKafkaDataList cartDataToKafka = new ToKafkaDataList();
        cartDataToKafka.setType("cart");
        cartDataToKafka.setTime(LocalDateTime.now());
        cartDataToKafka.setMessage(message);
        cartDataToKafka.setUsername(username);
        cartDataToKafka.setUserIP(ip);
        CommodityRankData commodityRankData = cartMapping.selectCommdityByID(id);
        commodityRankData.setQuantity(quantity);
        cartDataToKafka.setData(List.of(commodityRankData));
        dataProcess.sendDataToKafka(kafkaTemplate, id.toString(), cartDataToKafka, "GoodsRealTime");
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

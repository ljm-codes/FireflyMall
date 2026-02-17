package com.fm.Tool.AOP;

import POJO.DataList;
import POJO.Login.UserContext;
import POJO.ODS.CommodityRankData;
import POJO.Result;
import POJO.ToKafkaDataList;
import POJO.commodity.commodity;
import POJO.commodity.goods;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class ProductAOPTool {

    private final DataProcess<String, Object> dataProcess;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // java21+ 虚拟线程创建
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @AfterReturning(pointcut = "execution(* com.fm.controller.showCommodity.showGoodsById(..))", returning = "result")
    public void dataSendToKafka(JoinPoint joinPoint, Result result) {

        try {
            if (result.getCode() != 200 || result.getData() == null) {
                log.error("showGoodsById,请求失败，或者没找到该商品");
                return;
            }

            String goodsIdStr = (String) joinPoint.getArgs()[0];
            String username = UserContext.getUserInfo().getUsername();
            Long goodsId = Long.parseLong(goodsIdStr);
            String ip = UserContext.getUserInfo().getUserIP();
            goods goods = (goods) result.getData();

            log.info("用户IP:{}", ip);

            executor.execute(() -> sendGoodsDataToKafka(goodsId, goods, ip, username));
        } catch (Exception e) {
            log.error("AOP拦截showGoodsById后发送Kafka数据异常: {}", e.getMessage());
        }

    }

    @AfterReturning(pointcut = "execution(* com.fm.controller.showCommodity.searchGoods(..))", returning = "result")
    public void searchGoodsDataSendToKafka(JoinPoint joinPoint, Result result) {
        try {
            if (result.getCode() != 200) {
                log.error("searchGoods,请求失败，或者没找到该商品");
                return;
            }

            String keyword = (String) joinPoint.getArgs()[0];
            String username = UserContext.getUserInfo().getUsername();
            String ip = UserContext.getUserInfo().getUserIP();
            DataList<commodity> commodityList = Convert.convert(new TypeReference<>() {
            }, result.getData());

            executor.execute(() -> sendSearchDataToKafka(keyword, ip, username, commodityList));

        } catch (Exception e) {
            log.error("AOP拦截searchGoods后发送Kafka数据异常: {}", e.getMessage());
        }
    }

    private void sendSearchDataToKafka(String keyword, String ip, String username, DataList<commodity> commodityList) {
        // 过滤商品，只要想要的商品字段
        List<CommodityRankData> filteredCommodityList = commodityList.getData().stream()
                .map(commodity -> new CommodityRankData(
                        commodity.getId(),
                        -1,
                        commodity.getPrice(),
                        commodity.getImage(),
                        commodity.getCategory(),
                        commodity.getBrand(),
                        commodity.getSpec(),
                        commodity.getSold(),
                        commodity.isAD() ? 1 : 0
                ))
                .toList();

        ToKafkaDataList kafkaData = new ToKafkaDataList();
        kafkaData.setType("search");
        kafkaData.setKeyword(keyword);
        kafkaData.setTime(LocalDateTime.now());
        kafkaData.setUsername(username);
        kafkaData.setUserIP(ip);
        kafkaData.setData(filteredCommodityList);

        dataProcess.sendDataToKafka(kafkaTemplate, ip, kafkaData, "GoodsRealTime");

    }

    private void sendGoodsDataToKafka(Long goodsId, goods goods, String ip, String username) {
        ToKafkaDataList kafkaData = new ToKafkaDataList();
        kafkaData.setType("id");
        kafkaData.setTime(LocalDateTime.now());
        kafkaData.setUsername(username);
        kafkaData.setUserIP(ip);
        kafkaData.setData(List.of(new CommodityRankData(goodsId, -1, goods.getPrice(), goods.getImage(), goods.getCategory(), goods.getBrand(), goods.getSpec(), goods.getSold(), goods.getIsAD())));
        dataProcess.sendDataToKafka(kafkaTemplate, goodsId.toString(), kafkaData, "GoodsRealTime");
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

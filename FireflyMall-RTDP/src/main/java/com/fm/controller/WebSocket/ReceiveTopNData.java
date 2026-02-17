package com.fm.controller.WebSocket;

import POJO.ADS.*;
import POJO.DataList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiveTopNData {

    private final RTDPWebsocketHandler rtdpWebsocketHandler;

    @KafkaListener(
            topics = {"idTopN", "cartAdd", "cartDelete", "createOrderTopN"},
            properties = {
                    "spring.json.value.default.type: POJO.DataList"
            }
    )
    public void receiveIdTopNData(ConsumerRecord<String, DataList<TopNData>> record) {
        String topic = record.topic();
        if (record.value() == null) {
            log.error("在 receiveIdTopNData 消费到的 {} 消息为空", topic);
            return;
        }

        DataList<TopNData> dataList = record.value();

        log.info("在 receiveIdTopNData 中的 {} 的数据为：{}", topic, dataList);

        rtdpWebsocketHandler.sendTextMessageToAll(dataList, topic, "/realTimeTopN/" + topic);
    }

    @KafkaListener(
            topics = "searchTopN",
            properties = {
                    "spring.json.value.default.type: POJO.DataList"
            }
    )
    public void receiveSearchTopNData(ConsumerRecord<String, DataList<TopNDataTargetSearch>> record) {
        if (record.value() == null) {
            log.error("消费到的 searchTopN 消息为空");
            return;
        }

        DataList<TopNDataTargetSearch> dataList = record.value();

        log.info("searchTopN的数据为：{}", dataList);

        rtdpWebsocketHandler.sendTextMessageToAll(dataList, "searchTopNData", "/realTimeTopN/search");
    }

    @KafkaListener(
            topics = {"categoryTopN", "brandTopN"},
            properties = {
                    "spring.json.value.default.type: POJO.DataList"
            }
    )
    public void receiveCategoryTopNData(ConsumerRecord<String, DataList<CategoryOrBrandTopNData>> record) {
        String topic = record.topic();

        if (record.value() == null) {
            log.error("消费到的 {} 消息为空", topic);
            return;
        }

        DataList<CategoryOrBrandTopNData> dataList = record.value();

        log.info("{}的数据为：{}", topic, dataList);

        rtdpWebsocketHandler.sendTextMessageToAll(dataList, topic, "/realTimeTopN/" + topic);
    }

    @KafkaListener(
            topics = "userIpRegionTopN",
            properties = {
                    "spring.json.value.default.type: POJO.DataList"
            }
    )
    public void receiveUserIpRegionTopNData(ConsumerRecord<String, DataList<userIpAndUsername>> record) {
        if (record.value() == null) {
            log.error("消费到的 userIpRegionTopN 消息为空");
            return;
        }

        DataList<userIpAndUsername> dataList = record.value();

        log.info("userIpRegionTopN的数据为：{}", dataList);

        rtdpWebsocketHandler.sendTextMessageToAll(dataList, "userIpRegionTopN", "/realTimeTopN/userIpRegionTopN");
    }

    @KafkaListener(
            topics = "convertCount",
            properties = {
                    "spring.json.value.default.type: POJO.ADS.FunnelResult"
            }
    )
    public void receiveConvertCountData(ConsumerRecord<String, FunnelResult> record) {
        if (record.value() == null) {
            log.error("消费到的 convertCount 消息为空");
            return;
        }

        FunnelResult data = record.value();

        log.info("convertCount的数据为：{}", data);

        rtdpWebsocketHandler.sendTextMessageToAll(data, "convertCount", "/realTimeTopN/convertCount");
    }

    @KafkaListener(
            topics = "intervalTime",
            properties = {
                    "spring.json.value.default.type: POJO.ADS.IntervalResult"
            }
    )
    public void receiveIntervalTimeData(ConsumerRecord<String, IntervalResult> record) {
        if (record.value() == null) {
            log.error("消费到的 intervalTime 消息为空");
            return;
        }

        IntervalResult data = record.value();

        log.info("intervalTime的数据为：{}", data);

        rtdpWebsocketHandler.sendTextMessageToAll(data, "intervalTime", "/realTimeTopN/intervalTime");
    }

    @KafkaListener(
            topicPattern = "*Rate",
            properties = {
                    "spring.json.value.default.type: POJO.ADS.SuccessRateWindowResult"
            }
    )
    public void receiveRateData(ConsumerRecord<String, SuccessRateWindowResult> record) {
        if (record.value() == null) {
            log.error("消费到的 Rate 消息为空");
            return;
        }

        String topic = record.topic();

        SuccessRateWindowResult data = record.value();

        log.info("Rate的数据为：{}", data);

        rtdpWebsocketHandler.sendTextMessageToAll(data, topic, "/realTimeTopN/" + topic);
    }

    @KafkaListener(
            topics = "orderAmountTopN",
            properties = {
                    "spring.json.value.default.type: java.util.List"
            }
    )
    public void receiveOrderAmountTopNData(ConsumerRecord<String, List<Amount>> record) {
        if (record.value() == null) {
            log.error("消费到的 orderAmountTopN 消息为空");
            return;
        }

        List<Amount> data = record.value();

        log.info("orderAmountTopN的数据为：{}", data);

        rtdpWebsocketHandler.sendTextMessageToAll(data, "orderAmountTopN", "/realTimeTopN/orderAmountTopN");
    }

    @KafkaListener(
            topics = "payStatus",
            properties = {
                    "spring.json.value.default.type: java.util.Map"
            }
    )
    public void receivePayStatusData(ConsumerRecord<String, Map<String, String>> record) {
        if (record.value() == null) {
            log.error("消费到的 payStatus 消息为空");
            return;
        }

        Map<String, String> data = record.value();

        log.info("payStatus的数据为：{}", data);

        rtdpWebsocketHandler.sendTextMessageToAll(data, "payStatus", "/realTimeTopN/payStatus");
    }

    @KafkaListener(
            topicPattern = "*UserRecommendData",
            properties = {
                    "spring.json.value.default.type: POJO.ADS.RecommendedResults"
            }
    )
    public void realTimeRecommendation(ConsumerRecord<String, RecommendedResults> record) {
        String topic = record.topic();
        if (record.value() == null) {
            log.error("在主题{}，中无法消费到数据", topic);
            return;
        }

        RecommendedResults data = record.value();

        log.info("在主题 {} 中消费到数据：{}", topic, data);

        String userId = data.getUserId();

        rtdpWebsocketHandler.sendTextMessageToUserById(userId, "Recommendation", "/Recommendation/" + topic, data);

    }
}

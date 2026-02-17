package Tools.KafkaTool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class DataProcess<K, V> {

    public void sendDataToKafka(KafkaTemplate<K, V> kafkaTemplate, K key, V value, String topic) {
        CompletableFuture<SendResult<K, V>> send = kafkaTemplate.send(topic, key, value);
        send.whenComplete((result, e) -> {
            if (e != null) {
                log.error("发送数据到Kafka失败", e);
            }else{
                log.info("发送数据到Kafka成功，offset: {}", result.getRecordMetadata().offset());
            }
        });
    }

}

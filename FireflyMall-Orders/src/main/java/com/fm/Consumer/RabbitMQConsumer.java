package com.fm.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("order"),
            exchange = @Exchange(name = "order_exchange", delayed = "true"),
            key = {"order"}
    ))
    public void consume(Long orderId) {
        log.info("收到订单过期ID: {}", orderId);
        // 30分钟删除订单
        redisTemplate.delete("order:" + orderId.toString());
    }

}

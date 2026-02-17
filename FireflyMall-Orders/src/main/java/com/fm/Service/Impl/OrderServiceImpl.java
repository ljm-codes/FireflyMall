package com.fm.Service.Impl;

import POJO.DataList;
import POJO.Login.UserContext;
import POJO.Order.OrderInfo;
import POJO.Order.OrderItem;
import POJO.Result;
import POJO.commodity.goods;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.fm.Mapper.OrderMapper;
import com.fm.Service.OrderService;
import com.fm.client.OrderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderClient orderClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderMapper orderMapper;
    private final RabbitTemplate rabbitTemplate;

    private final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    @Override
    @Transactional
    public Map<String, Object> createOrder(OrderInfo orderInfo, Long userId) {
        String exchangeName = "order_exchange";

        Result checkCartResult = orderClient.checkCart();
        if (checkCartResult.getCode() == 400) {
            log.error("购物车商品不存在");
            throw new IllegalArgumentException("购物车商品不存在");
        }

        // 根据购物车商品信息创建订单
        try {
            Object goodsList = checkCartResult.getData();
            log.info("购物车商品信息: {}", goodsList);
            // 利用Hutool的Convert工具将购物车商品信息转换为List<goods>
            List<goods> cartGoodsList = Convert.toList(goods.class, goodsList);
            // 生成订单ID
            Long orderId = snowflake.nextId();
            // 从购物车商品列表中提取商品ID和购买数量
            List<Long> goodsIds = cartGoodsList.stream().map(goods::getId).toList();
            List<Integer> quantities = cartGoodsList.stream().map(goods::getQuantity).toList();

            Double totalAmount = cartGoodsList.stream()
                    .mapToDouble(g -> g.getPrice() * g.getQuantity())
                    .sum();
            orderInfo.setTotalAmount(totalAmount);

            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("goodsIds", goodsIds);
            map.put("quantities", quantities);
            map.put("totalAmount", totalAmount);
            log.info("创建订单，订单ID：{}，商品ID：{}，购买数量：{}", orderId, goodsIds, quantities);
            // 缓存订单信息，过期时间30分钟
            String jsonData = JSONUtil.toJsonStr(map);
            redisTemplate.opsForValue().set("order:" + orderId, jsonData, 31, TimeUnit.MINUTES);
            orderInfo.setOrderId(orderId);
            orderInfo.setUserId(userId);
            log.info("订单信息：{}", orderInfo);
            // 调用订单微服务创建订单
            orderMapper.createOrder(orderInfo);
            // 发送订单ID到消息队列，设置延迟时间为30分钟
            rabbitTemplate.convertAndSend(exchangeName, "order", orderId, message -> {
                message.getMessageProperties().setDelayLong(30 * 60 * 1000L); // 30分钟。测试时先用10秒
                return message;
            });
            // 调用订单微服务删除购物车商品
            redisTemplate.opsForHash().delete("cart:" + userId.toString());
            return map;
        } catch (Exception e) {
            log.error("创建订单失败：{}", e.getMessage());
            throw new IllegalArgumentException("创建订单失败");
        }

    }

    @Override
    public DataList<goods> getOrder(Long orderId) {
        DataList<goods> orderGoodsList = new DataList<>();
        try {
            String orderData = (String) redisTemplate.opsForValue().get(orderId.toString());
            if (orderData == null) {
                log.info("延迟订单不存在，查看MySQL中是否存在订单");
                Long userId = UserContext.getUserInfo().getId();
                List<OrderItem> oldOrderItemList = orderMapper.getOrderItem(orderId, userId);
                if (oldOrderItemList.isEmpty()) {
                    log.error("订单不存在");
                    throw new IllegalArgumentException("订单不存在");
                }
                List<Long> goodsIds = oldOrderItemList.stream().map(OrderItem::getGoodsId).toList();
                List<Integer> quantities = oldOrderItemList.stream().map(OrderItem::getQuantity).toList();
                List<goods> GoodsList = orderMapper.getGoods(goodsIds);
                for (goods goods : GoodsList) {
                    goods.setQuantity(quantities.get(GoodsList.indexOf(goods)));
                }
                int totalAmount = GoodsList.stream()
                        .mapToInt(g -> g.getPrice() * g.getQuantity())
                        .sum();
                orderGoodsList.setData(GoodsList);
                orderGoodsList.setTotalPrice(totalAmount);
                orderGoodsList.setTotalQuantity(quantities.stream().mapToInt(Integer::intValue).sum());
                return orderGoodsList;
            }

            log.info("得到的订单信息：{}", orderData);

            Map<String, Object> orderMap = JSONUtil.toBean(orderData, Map.class);
            List<Long> goodsIds = Convert.toList(Long.class, orderMap.get("goodsIds"));
            List<Integer> quantities = Convert.toList(Integer.class, orderMap.get("quantities"));
            Integer totalAmount = Convert.toInt(orderMap.get("totalAmount"));
            Integer totalQuantity = quantities.stream().mapToInt(Integer::intValue).sum();
            List<goods> GoodsList = orderMapper.getGoods(goodsIds);
            for (goods goods : GoodsList) {
                goods.setQuantity(quantities.get(GoodsList.indexOf(goods)));
            }
            orderGoodsList.setData(GoodsList);
            orderGoodsList.setTotalPrice(totalAmount);
            orderGoodsList.setTotalQuantity(totalQuantity);
            return orderGoodsList;
        } catch (Exception e) {
            log.error("获取订单失败：{}", e.getMessage());
            throw new IllegalArgumentException("获取订单失败");
        }
    }

    @Override
    public List<OrderInfo> getOrderRoughInfoList() {
        try {
            Long userId = UserContext.getUserInfo().getId();
            List<OrderInfo> orderInfoList = orderMapper.getOrderRoughInfoList(userId);
            log.info("用户{}的订单粗略信息列表：{}", userId, orderInfoList);
            if (orderInfoList.isEmpty()) {
                log.error("用户没有订单");
                throw new IllegalArgumentException("用户没有订单");
            }
            return orderInfoList;
        } catch (Exception e) {
            log.error("获取订单粗略信息列表失败：{}", e.getMessage());
            throw new IllegalArgumentException("获取订单粗略信息列表失败");
        }
    }

    @Override
    @Transactional
    public void cancelOrderStatus(Long orderId) {
        try {
            orderMapper.updateOrderStatus(orderId, 2);
            String orderData = (String) redisTemplate.opsForValue().get(orderId.toString());
            if (orderData == null) {
                log.error("延迟订单不存在，无法取消订单");
                throw new IllegalArgumentException("延迟订单不存在，无法取消订单");
            }
            Map<String, Object> orderMap = JSONUtil.toBean(orderData, Map.class);
            List<Long> goodsIds = Convert.toList(Long.class, orderMap.get("goodsIds"));
            List<Integer> quantities = Convert.toList(Integer.class, orderMap.get("quantities"));
            Long userId = UserContext.getUserInfo().getId();
            List<Map<String, Object>> items = new ArrayList<>();
            for (int i = 0; i < goodsIds.size(); i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("goodsId", goodsIds.get(i));
                item.put("quantity", quantities.get(i));
                items.add(item);
            }

            orderMapper.addOldOrderItem(orderId, userId, items);

            // 从Redis中删除订单
            redisTemplate.delete(orderId.toString());
        } catch (Exception e) {
            log.error("取消订单失败：{}", e.getMessage());
            throw new IllegalArgumentException("取消订单失败");
        }
    }

    @Override
    @Transactional
    public void confirmOrderStatus(Long orderId) {
        try {
            LocalDateTime payTime = orderMapper.getPayTime(orderId);
            if (payTime == null) {
                log.error("你还未支付订单，无法确认收货");
                throw new IllegalArgumentException("你还未支付订单，无法确认收货");
            }
            LocalDateTime now = LocalDateTime.now();
            orderMapper.confirmOrderStatus(orderId, now);
        } catch (Exception e) {
            log.error("确认收货失败：{}", e.getMessage());
            throw new IllegalArgumentException("确认收货失败");
        }
    }

}

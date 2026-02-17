package com.fm.Service.Impl;

import POJO.Cart.shoppingCart;
import POJO.Login.UserContext;
import POJO.Login.UserInfo;
import POJO.commodity.goods;
import com.fm.Mapper.CartMapping;
import com.fm.Service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapping cartMapping;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void addToCart(shoppingCart cart) {

        goods goods = cartMapping.selectStockAndStatusAndPrice(cart.getId());
        if (goods.getStatus() != 1 && goods.getStock() < cart.getQuantity()) {
            throw new RuntimeException("商品状态错误或库存不足");
        }

        cartMapping.ReduceInventory(cart.getId(), cart.getQuantity());
        UserInfo userInfo = UserContext.getUserInfo();
        log.info("用户ID:{}", userInfo.getId());
        redisTemplate.opsForHash().put(
                "cart:" + userInfo.getId().toString()
                , cart.getId().toString()
                , cart.getQuantity().toString()
        );

    }

    @Override
    public List<goods> getCart() {
        UserInfo userInfo = UserContext.getUserInfo();
        log.info("userInfo:{}", userInfo);
        List<goods> goodsList;
        Map<Object, Object> cartItems;
        cartItems = redisTemplate.opsForHash().entries("cart:" + userInfo.getId().toString());
        log.info("购物信息:{}", cartItems);
        if (cartItems.isEmpty()) {
            return null;
        }

        Map<String, Integer> quantityMap = new HashMap<>();
        cartItems.forEach((key, value) -> {
            String goodsId = key.toString();
            Integer quantity = Integer.valueOf(value.toString());
            quantityMap.put(goodsId, quantity);
        });

        List<Long> cartIds = quantityMap.keySet().stream()
                .map(Long::parseLong)
                .toList();

        goodsList = cartMapping.selectCartProductItemsBatch(cartIds);

        goodsList.forEach(goods -> goods.setQuantity(quantityMap.get(String.valueOf(goods.getId()))));
        return goodsList;
    }

    @Override
    @Transactional
    public void updateCart(shoppingCart cart) {
        UserInfo userInfo = UserContext.getUserInfo();
        // 先得到当前购物车的对应的商品信息
        log.info("要更新购物车商品信息：{}", redisTemplate.opsForHash().get(userInfo.getId().toString(), cart.getId().toString()));
        Integer quantity = Integer.valueOf(
                Objects.requireNonNull(
                        redisTemplate.opsForHash()
                                .get(userInfo.getId().toString()
                                        , cart.getId().toString()
                                )
                ).toString()
        );
        // 查看当前购物车与更改后的商品数量的差值
        Integer diff = cart.getQuantity() - quantity; // diff>0 表示增加了商品数量，diff<0 表示减少了商品数量
        // 查看当前购物车与更改后的商品数量的差值是否会导致库存不足
        goods goods = cartMapping.selectStockAndStatusAndPrice(cart.getId());
        if (diff > 0) {
            if (goods.getStatus() != 1 && goods.getStock() < diff) {
                throw new RuntimeException("商品状态错误或库存不足");
            }
        }

        cartMapping.ReduceInventory(cart.getId(), diff);

        redisTemplate.opsForHash().put(
                userInfo.getId().toString()
                , cart.getId().toString()
                , cart.getQuantity().toString()
        );
    }

    @Override
    @Transactional
    public void deleteAllCart() {
        UserInfo userInfo = UserContext.getUserInfo();
        Map<Object, Object> carts = redisTemplate.opsForHash().entries(userInfo.getId().toString());
        log.info("要删除购物车商品信息：{}", carts);
        Map<String, Integer> cartMap = new HashMap<>();
        carts.forEach((key, value) -> {
            String goodsId = key.toString();
            Integer quantity = Integer.valueOf(value.toString());
            cartMap.put(goodsId, quantity);
        });
        // 遍历购物车中的商品，将库存增加
        cartMap.forEach((goodsId, quantity) -> cartMapping.IncreaseInventory(Long.parseLong(goodsId), quantity));
        // 清空购物车
        redisTemplate.delete(userInfo.getId().toString());
    }
}

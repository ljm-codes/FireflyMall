package com.fm.client;

import POJO.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "FireflyMall-cart")
public interface OrderClient {

    /**
     * 检查购物车商品是否存在
     *
     * @return 购物车商品信息
     */
    @GetMapping("/cart")
    Result checkCart();

    @DeleteMapping("/cart")
    Result deleteCart();
}

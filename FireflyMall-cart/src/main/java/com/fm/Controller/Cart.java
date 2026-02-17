package com.fm.Controller;

import POJO.Cart.shoppingCart;
import POJO.Result;
import POJO.commodity.goods;
import com.fm.Service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class Cart {

    private final CartService cartService;

    @PostMapping
    public Result addToCart(@RequestBody shoppingCart cart){
        log.info("添加商品到购物车");
        log.info("购物车商品信息：{}", cart);
        try {
            cartService.addToCart(cart);
        } catch (Exception e) {
            return Result.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        return Result.success();
    }

    @GetMapping
    public Result getCart(){
        log.info("获取购物车商品信息");
        try {
            List<goods> cartGoodsList  = cartService.getCart();
            return Result.success(cartGoodsList);
        } catch (Exception e) {
            log.error("获取购物车商品信息失败：", e);
            return Result.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @PutMapping
    public Result updateCart(@RequestBody shoppingCart cart){
        log.info("更新购物车商品信息：{}", cart);
        if (cart.getQuantity() <= 0) {
            return Result.error(HttpStatus.BAD_REQUEST,"商品数量不能小于等于0");
        }
        try {
            cartService.updateCart(cart);
            return Result.success();
        } catch (Exception e) {
            return Result.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @DeleteMapping
    public Result deleteCart(){
        log.info("删除购物车商品信息");
        try {
            cartService.deleteAllCart();
            return Result.success();
        } catch (Exception e) {
            return Result.error(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

}

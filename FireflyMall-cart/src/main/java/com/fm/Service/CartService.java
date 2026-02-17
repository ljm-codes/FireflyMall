package com.fm.Service;

import POJO.Cart.shoppingCart;
import POJO.commodity.goods;

import java.util.List;

public interface CartService {
    void addToCart(shoppingCart cart);

    List<goods> getCart();

    void updateCart(shoppingCart cart);

    void deleteAllCart();
}

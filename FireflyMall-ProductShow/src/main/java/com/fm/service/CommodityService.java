package com.fm.service;

import POJO.DataList;
import POJO.commodity.commodity;
import POJO.commodity.goods;

import java.io.IOException;

public interface CommodityService {

    DataList<goods> showGoodsList(Integer pageNum, Integer pageSize, String category, String sort);

    goods showGoodsById(String id);

    DataList<commodity> searchGoods(String keyword, Integer pageNum, Integer pageSize, String sort) throws IOException;
}

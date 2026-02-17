package com.fm.mapper;

import POJO.commodity.goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommodityMapper {

    List<goods> showGoodsList(int offset, Integer pageSize, String category, String field, String order);

    Integer countGoods(String category);

    goods showGoodsById(String id);
}

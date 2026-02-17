package com.fm.Mapper;

import POJO.ODS.CommodityRankData;
import POJO.commodity.goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapping {

    goods selectStockAndStatusAndPrice(Long id);

    void ReduceInventory(Long id, Integer quantity);

    List<goods> selectCartProductItemsBatch(List<Long> ids);

    void IncreaseInventory(long id, Integer quantity);

    CommodityRankData selectCommdityByID(Long id);
}

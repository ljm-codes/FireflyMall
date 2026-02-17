package com.fm.controller;

import POJO.Result;
import POJO.commodity.commodity;
import POJO.commodity.goods;
import POJO.DataList;
import com.fm.service.CategoryService;
import com.fm.service.CommodityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class showCommodity {

    private final CommodityService commodityService;
    private final CategoryService categoryService;

    @GetMapping
    public Result showGoodsList(Integer pageNum, Integer pageSize, String category, String sort) {
        log.info("页码：{}，每页数量：{}", pageNum, pageSize);
        if (pageNum == null || pageSize == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "页码或每页数量不能为空");
        }
        DataList<goods> goodsList = commodityService.showGoodsList(pageNum, pageSize, category, sort);
        return Result.success(goodsList);
    }

    @GetMapping("/{id}")
    public Result showGoodsById(@PathVariable String id) {
        log.info("商品ID：{}", id);
        goods goods = commodityService.showGoodsById(id);
        if (goods == null) {
            return Result.error(HttpStatus.NOT_FOUND, "商品不存在");
        }
        return Result.success(goods);
    }

    @GetMapping("/search")
    public Result searchGoods(String keyword, Integer pageNum, Integer pageSize, String sort) throws IOException {
        log.info("搜索关键词：{}", keyword);
        if (pageNum == null || pageSize == null || keyword == null) {
            return Result.error(HttpStatus.BAD_REQUEST, "页码或每页数量或搜索关键词不能为空");
        }
        DataList<commodity> commodityList = commodityService.searchGoods(keyword, pageNum, pageSize, sort);
        if (commodityList.getData().isEmpty()) {
            return Result.error(HttpStatus.NOT_FOUND, "商品不存在");
        }
        return Result.success(commodityList);
    }

    @GetMapping("/brand")
    public Result getCategoryAndBrand(String categoryName) throws IOException {
        log.info("获取商品品牌");
        if (categoryName == null || categoryName.isEmpty()) {
            return Result.error(HttpStatus.NOT_FOUND, "分类名称不能为空");
        }
        DataList<String> categoryList = categoryService.getCategoryAndBrand(categoryName);
        return Result.success(categoryList);
    }

    @GetMapping("/category")
    public Result getCategory() throws IOException {
        log.info("获取商品分类");
        DataList<String> categoryList = categoryService.getCategory();
        return Result.success(categoryList);
    }

}

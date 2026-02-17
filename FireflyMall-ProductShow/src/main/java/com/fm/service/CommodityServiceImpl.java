package com.fm.service;

import POJO.DataList;
import POJO.commodity.commodity;
import POJO.commodity.goods;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fm.mapper.CommodityMapper;
import com.fm.service.CommodityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommodityServiceImpl implements CommodityService {

    private final CommodityMapper commodityMapper;

    private final ElasticsearchClient client;

    @Override
    public DataList<goods> showGoodsList(Integer pageNum, Integer pageSize, String category, String sort) {
        DataList<goods> dataList = new DataList<>();
        dataList.setPage(pageNum);
        dataList.setSize(pageSize);
        int offset = (pageNum - 1) * pageSize;
        // sort : 字段_排序方式(asc/desc)
        if (sort == null || sort.isEmpty()) {
            // 默认按商品ID排序
            sort = "id_asc";
        }
        String[] strings = sort.split("_");
        String field = strings[0];
        String order = strings[1];
        dataList.setData(commodityMapper.showGoodsList(offset, pageSize, category, field, order));
        dataList.setTotal(commodityMapper.countGoods(category));
        return dataList;
    }

    @Override
    public goods showGoodsById(String id) {
        return commodityMapper.showGoodsById(id);
    }

    @Override
    public DataList<commodity> searchGoods(String keyword, Integer pageNum, Integer pageSize, String sort) throws IOException {
        String indexName = "goods";
        DataList<commodity> data = new DataList<>();
        // 解析排序字段和排序方式
        if (sort == null || sort.isEmpty()) {
            // 默认按商品ID排序
            sort = "id_asc";
        }
        String[] strings = sort.split("_");
        String field = strings[0];
        String order = strings[1];
        SearchResponse<commodity> response = client.search(s -> s
                        .index(indexName)
                        .query(q -> q
                                .match(m -> m
                                        .field("name")
                                        .query(keyword)
                                )
                        )
                        .from((pageNum - 1) * pageSize)
                        .size(pageSize)
                        .sort(so -> so
                                .field(f -> f.field(field)
                                        .order(order.equals("asc") ? SortOrder.Asc : SortOrder.Desc))
                        )
                        .highlight(h -> h.fields("name", f -> f.preTags("<em>").postTags("</em>")))
                , commodity.class
        );

        List<commodity> commodityList = response.hits().hits().stream().map(hit -> {
            commodity item = hit.source();
            if (item != null) {
                Map<String, List<String>> highlightFields = hit.highlight();
                log.info("高亮Map: {}", highlightFields);
                if (highlightFields != null && highlightFields.containsKey("name")) {
                    List<String> nameHighlights = highlightFields.get("name");
                    log.info("高亮字段: {}", nameHighlights);
                    if (nameHighlights != null && !nameHighlights.isEmpty()) {
                        item.setHighlightString(nameHighlights.getFirst());
                    }
                }
            }
            return item;
        }).collect(Collectors.toList());

        data.setData(commodityList);
        data.setPage(pageNum);
        data.setSize(pageSize);
        if (response.hits().total() != null) {
            data.setTotal((int) response.hits().total().value());
        }
        return data;
    }
}

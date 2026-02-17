package com.fm.service;

import POJO.DataList;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fm.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ElasticsearchClient client;

    @Override
    public DataList<String> getCategoryAndBrand(String categoryName) throws IOException {
        String indexName = "goods";
        SearchResponse<Void> CategoryAndBrand = client.search(s -> s.index(indexName)
                        .query(q -> q.bool(b -> b
                                        .must(m -> m.term(t -> t.field("status").value(1)))
                                        .must(m -> m.term(t -> t.field("category").value(categoryName)))
                                )
                        )
                        .aggregations("Brand", a -> a
                                .terms(t -> t.field("brand").size(100))
                        )
                        .size(0)
                , Void.class);

        return dataList("Brand", CategoryAndBrand);
    }

    @Override
    public DataList<String> getCategory() throws IOException {
        String indexName = "goods";
        SearchResponse<Void> Category = client.search(s -> s.index(indexName)
                        .query(q -> q.bool(b -> b
                                        .must(m -> m.term(t -> t.field("status").value(1)))
                                )
                        )
                        .aggregations("Category", a -> a
                                .terms(t -> t.field("category").size(1000))
                        )
                        .size(0)
                , Void.class);
        return dataList("Category", Category);
    }


    private DataList<String> dataList(String data, SearchResponse<Void> response){
        DataList<String> dataList = new DataList<>();
        List<String> list = new ArrayList<>();
        Map<String, Aggregate> aggregations = response.aggregations();
        if (aggregations.containsKey(data)) {
            Aggregate categoryAggregate = aggregations.get(data);
            if(categoryAggregate!=null){
                StringTermsAggregate sterms = categoryAggregate.sterms();
                for (StringTermsBucket bucket : sterms.buckets().array()) {
                    if(bucket.key().stringValue().isEmpty()) {
                        continue;
                    }
                    list.add(bucket.key().stringValue());
                }
            }
        }
        dataList.setData(list);
        return dataList;
    }

}

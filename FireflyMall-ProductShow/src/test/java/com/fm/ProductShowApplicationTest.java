//package com.fm;
//
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch._types.SortOrder;
//import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
//import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
//import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
//import co.elastic.clients.elasticsearch.core.*;
//import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
//import co.elastic.clients.elasticsearch.core.search.Hit;
//import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
//import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
//import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
//import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fm.Pojo.Goods;
//import com.fm.Pojo.commodity;
//import com.fm.Pojo.user;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.platform.commons.util.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Slf4j
//@SpringBootTest(classes = ProductShowApplication.class)
//class ProductShowApplicationTest {
//    @Autowired
//    private ElasticsearchClient client;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private static final int BATCH_SIZE = 1000; // 每批处理1000条
//
//    @Test
//    void setUp() throws IOException {
//        // 测试1: 检查集群信息
//        InfoResponse info = client.info();
//
//        assertNotNull(info);
//        assertTrue(info.version().number().contains("8.13"));
//        log.info("✅ Elasticsearch 版本: {}", info.version().number());
//        log.info("✅ 集群名称: {}", info.clusterName());
//        log.info("✅ 节点名称: {}", info.name());
//        log.info("✅ 标签: {}", info.tagline());
//        log.info("✅ 集群 UUID: {}", info.clusterUuid());
//    }
//
//    @Test
//    void testAddCommodity() throws IOException {
//        String indexName = "goods";
//        CreateIndexResponse response = client.indices()
//                .create(c -> c.index(indexName)
//                        .mappings(m -> m
//                                .properties("id", p -> p.keyword(k -> k
//                                        .index(true)
//                                ))
//                                .properties("name", p -> p.text(k -> k
//                                        .analyzer("ik_max_word").searchAnalyzer("ik_smart")
//                                ))
//                                .properties("price", p -> p.integer(k -> k))
//                                .properties("stock", p -> p.integer(k -> k))
//                                .properties("image", p -> p.keyword(k -> k.index(false)))
//                                .properties("category", p -> p.keyword(k -> k))
//                                .properties("brand", p -> p.keyword(k -> k
//                                        .index(true)
//                                ))
//                                .properties("spec", p -> p.flattened(k -> k))
//                                .properties("sold", p -> p.integer(k -> k))
//                                .properties("isAD", p -> p.boolean_(b -> b
//                                        .index(true)
//                                ))
//                                .properties("status", p -> p.integer(k -> k))
//                        )
//                );
//        log.info("✅ 创建索引: {}", response.index());
//        log.info("✅ 索引创建成功: {}", response.acknowledged());
//    }
//
//    @Test
//    void selectCommodity() throws IOException {
//        String indexName = "user";
//        boolean exists = client.indices().exists(e -> e.index(indexName)).value();
//        log.info("✅ 索引存在: {}", exists);
//    }
//
//    @Test
//    void testUpdateCommodity() throws IOException {
//        String indexName = "user";
//        PutMappingResponse age = client.indices().putMapping(m -> m.index(indexName)
//                .properties("age", p -> p.integer(i -> i
//                                .index(true)
//                        )
//                ));
//        log.info("✅ 索引映射更新成功: {}", age.acknowledged());
//    }
//
//    @Test
//    void deleteCommodity() throws IOException {
//        String indexName = "goods";
//        DeleteIndexResponse response = client.indices().delete(d -> d.index(indexName));
//        log.info("✅ 索引删除成功: {}", response.acknowledged());
//    }
//
//    @Test
//    void testGetIndex() throws Exception {
//        String indexName = "goods";
//
//        GetIndexResponse response = client.indices().get(g -> g
//                .index(indexName)
//        );
//
//        System.out.println("✅ 索引映射: " + Objects.requireNonNull(response.get(indexName)).mappings());
//    }
//
//    @Test
//    void testAddUser() throws IOException {
//        String indexName = "goods";
//        user user = new user();
//        user.setInfo("这是一个测试用户的信息");
//        user.setEmail("test@example.com");
//
//        user.Name name = new user.Name();
//        name.setFirstName("张");
//        name.setLastName("三");
//        user.setName(name);
//
//        IndexResponse response = client.index(i -> i
//                .index(indexName)
//                .id("1")
//                .document(user)
//        );
//
//        log.info("✅ 索引响应: {}", response);
//
//    }
//
//    @Test
//    void testGetUserById() throws IOException {
//        String indexName = "user";
//        String id = "1";
//        GetResponse<user> response = client.get(g -> g
//                        .index(indexName)
//                        .id(id)
//                , user.class
//        );
//        if (response.found()) {
//            user user = response.source();
//            log.info("✅ 用户信息: {}", user);
//        } else {
//            log.info("❌ 用户不存在");
//        }
//    }
//
//    @Test
//    void testGetUser() throws IOException {
//        String indexName = "goods";
//
//        SearchResponse<commodity> response = client.search(s -> s
//                        .index(indexName)
//                        .query(q -> q
//                                .match(m -> m
//                                        .field("name")
//                                        .query("小米")
//                                )
//                        )
//                        .from(0)
//                        .size(20)
//                        .sort(so -> so
//                                .field(f -> f.field("sold")
//                                        .order(SortOrder.Asc))
//                        )
//                , commodity.class
//        );
//
//        assertNotNull(response.hits().total());
//        log.info("查到的数量为：{}", response.hits().total().value());
//
//        for (Hit<commodity> hit : response.hits().hits()) {
//            commodity goods = hit.source();
//            log.info("商品信息: {}", goods);
//        }
//
//    }
//
//    @Test
//    public void syncAllGoodsToES() {
//        log.info("开始同步商品数据到ES...");
//        long startTime = System.currentTimeMillis();
//
//        int totalProcessed = 0;
//        long lastId = 0;
//
//        try {
//            while (true) {
//                // 分批查询MySQL
//                List<Goods> batchGoods = fetchGoodsBatch(lastId, BATCH_SIZE);
//                if (batchGoods.isEmpty()) {
//                    break;
//                }
//
//                // 批量导入ES
//                boolean success = bulkIndexToES(batchGoods);
//                if (success) {
//                    totalProcessed += batchGoods.size();
//                    lastId = batchGoods.get(batchGoods.size() - 1).getId();
//                    log.info("已处理: {}/{}", totalProcessed, getTotalCount());
//                } else {
//                    log.error("批量导入失败，最后处理的ID: {}", lastId);
//                    break;
//                }
//
//                // 小延迟，避免对数据库和ES造成过大压力
//                Thread.sleep(50);
//            }
//
//            long endTime = System.currentTimeMillis();
//            log.info("数据同步完成! 总计: {} 条, 耗时: {} 秒",
//                    totalProcessed, (endTime - startTime) / 1000);
//
//        } catch (Exception e) {
//            log.error("数据同步异常", e);
//        }
//    }
//
//    private List<Goods> fetchGoodsBatch(long lastId, int batchSize) {
//        String sql = """
//                SELECT id, name, price, stock, image, category, brand,
//                       spec, sold, isAD, status
//                FROM goods
//                WHERE id > ? AND status != 3  -- 排除已删除商品
//                ORDER BY id ASC
//                LIMIT ?
//                """;
//
//        return jdbcTemplate.query(sql, new Object[]{lastId, batchSize}, (rs, rowNum) -> {
//            Goods goods = new Goods();
//            goods.setId(rs.getLong("id"));
//            goods.setName(rs.getString("name"));
//            goods.setPrice(rs.getInt("price"));
//            goods.setStock(rs.getInt("stock"));
//            goods.setImage(rs.getString("image"));
//            goods.setCategory(rs.getString("category"));
//            goods.setBrand(rs.getString("brand"));
//            goods.setSpec(rs.getString("spec"));
//            goods.setSold(rs.getInt("sold"));
//            goods.setIsAD(rs.getInt("isAD"));
//            goods.setStatus(rs.getInt("status"));
//            return goods;
//        });
//    }
//
//    private boolean bulkIndexToES(List<Goods> goodsList) {
//        try {
//            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
//
//            for (Goods goods : goodsList) {
//                Map<String, Object> doc = convertToESDocument(goods);
//
//                bulkBuilder.operations(op -> op
//                        .index(idx -> idx
//                                .index("goods")
//                                .id(goods.getId().toString())
//                                .document(doc)
//                        )
//                );
//            }
//
//            BulkResponse response = client.bulk(bulkBuilder.build());
//
//            if (response.errors()) {
//                log.error("批量导入存在错误");
//                for (BulkResponseItem item : response.items()) {
//                    if (item.error() != null) {
//                        log.error("文档 {} 错误: {}", item.id(), item.error().reason());
//                    }
//                }
//                return false;
//            }
//
//            return true;
//
//        } catch (IOException e) {
//            log.error("批量导入IO异常", e);
//            return false;
//        }
//    }
//
//    private int getTotalCount() {
//        String sql = "SELECT COUNT(*) FROM goods WHERE status != 3";
//        return jdbcTemplate.queryForObject(sql, Integer.class);
//    }
//
//    private Map<String, Object> convertToESDocument(Goods goods) {
//        Map<String, Object> doc = new HashMap<>();
//        doc.put("id", goods.getId().toString());
//        doc.put("name", goods.getName());
//        doc.put("price", goods.getPrice());
//        doc.put("stock", goods.getStock());
//        doc.put("image", goods.getImage());
//        doc.put("category", goods.getCategory());
//        doc.put("brand", goods.getBrand());
//        doc.put("sold", goods.getSold());
//        doc.put("isAD", goods.getIsAD() == 1);
//        doc.put("status", goods.getStatus());
//
//        // 处理spec字段
//        if (StringUtils.isNotBlank(goods.getSpec())) {
//            try {
//                Map<String, Object> specMap = new ObjectMapper().readValue(goods.getSpec(), Map.class);
//                doc.put("spec", specMap);
//            } catch (Exception e) {
//                doc.put("spec", Collections.singletonMap("raw", goods.getSpec()));
//            }
//        }
//
//        return doc;
//    }
//
//    @Test
//    void testCategoryAndBrand() throws IOException {
//        String indexName = "goods";
//
//        SearchResponse<Void> CategoryAndBrand = client.search(s -> s.index(indexName)
//                        .query(q -> q.bool(b -> b
//                                        .must(m -> m.term(t -> t.field("status").value(1)))
//                                        .must(m -> m.term(t -> t.field("category").value("手机")))
//                                )
//                        )
////                        .aggregations("Category", a -> a
////                                .terms(t -> t.field("category").size(100))
////                        )
//                        .aggregations("Brand", a -> a
//                                .terms(t -> t.field("brand").size(100))
//                        )
//                        .size(0)
//                , Void.class);
//
//        Map<String, List<String>> categoryMap = new HashMap<>();
//        if (CategoryAndBrand == null) {
//            log.error("CategoryAndBrand 为空");
//            return;
//        }
//        Map<String, Aggregate> aggregations = CategoryAndBrand.aggregations();
//        if (aggregations == null) {
//            log.error("aggregations 为空");
//            return;
//        }
//        List<String> categoryList = new ArrayList<>();
//        if (aggregations.containsKey("Brand")) {
//            Aggregate brand = aggregations.get("Brand");
//            if(brand != null) {
//                StringTermsAggregate sterms = brand.sterms();
//                for (StringTermsBucket bucket : sterms.buckets().array()) {
//                    if(bucket.key().stringValue().isEmpty()) {
//                        continue;
//                    }
//                    log.info("品牌: {}", bucket.key().stringValue());
//                    categoryList.add(bucket.key().stringValue());
//                }
//            }
//        }
//        categoryMap.put("手机", categoryList);
//        log.info("解析后的数据为: {}", categoryMap);
//    }
//
//}
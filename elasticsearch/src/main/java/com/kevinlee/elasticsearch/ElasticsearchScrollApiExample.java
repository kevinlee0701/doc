//package com.kevinlee.elasticsearch;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch._types.Time;
//import co.elastic.clients.elasticsearch.core.*;
//import co.elastic.clients.elasticsearch.core.search.Hit;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//
//import java.io.IOException;
//
//public class ElasticsearchScrollExample {
//
//    public static void main(String[] args) throws IOException {
//        // 创建 RestClient 和 Transport
//        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//
//        // 使用 RestClientTransport 创建 ElasticsearchClient
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        // 调用滚动查询的方法
//        scrollSearch(client);
//
//        // 关闭 RestClient
//        restClient.close();
//    }
//
//    private static void scrollSearch(ElasticsearchClient client) throws IOException {
//        // 创建初始搜索请求
//        SearchRequest searchRequest = SearchRequest.of(s -> s
//                .index("your_index_name")
//                .scroll(Time.of(t -> t.time("1m"))) // 设置滚动时间为1分钟
//                .size(100) // 每次返回100条数据
//                .query(q -> q
//                        .matchAll(m -> m) // 查询所有文档
//                )
//        );
//
//        // 执行初次搜索并获取响应
//        SearchResponse<Void> searchResponse = client.search(searchRequest, Void.class);
//
//        // 获取初始的 Scroll ID 和结果
//        String scrollId = searchResponse.scrollId();
//        var hits = searchResponse.hits().hits();
//
//        // 滚动查询
//        while (hits != null && !hits.isEmpty()) {
//            // 处理每个文档
//            for (Hit<Void> hit : hits) {
//                System.out.println(hit.id());
//            }
//
//            // 执行滚动查询，获取下一批数据
//            SearchScrollRequest scrollRequest = SearchScrollRequest.of(r -> r
//                    .scrollId(scrollId)
//                    .scroll(Time.of(t -> t.time("1m")))
//            );
//            SearchResponse<Void> scrollResponse = client.scroll(scrollRequest, Void.class);
//
//            // 更新 Scroll ID 和结果
//            scrollId = scrollResponse.scrollId();
//            hits = scrollResponse.hits().hits();
//
//            // 如果没有更多数据，退出循环
//            if (hits.isEmpty()) {
//                break;
//            }
//        }
//
//        // 清除滚动上下文，释放资源
//        ClearScrollRequest clearScrollRequest = ClearScrollRequest.of(r -> r.scrollId(scrollId));
//        client.clearScroll(clearScrollRequest);
//    }
//}

package com.kevinlele.elasticsearch;


import com.ctrip.framework.foundation.Foundation;
import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.MDCExample;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;


@Slf4j
@SpringBootTest(classes = ElasticsearchApplication.class)
@RunWith(SpringRunner.class)
public class EsTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    String index ="logstash-log";
    String testId="49273";

    /**
     * 判断索引是否存在
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());
        builder.size(20);

        searchRequest.source(builder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            log.info("输出数据:" + iterator.next().getSourceAsString());
        }

    }
    @Test
    public void test2(){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }


    @Test
    public void test3(){
        MDC.put("userId", "123456");
        MDC.put("requestId", "987654");

        // 记录日志
        log.info("This is a log message with MDC context.");
    }

    @Test
    public void test4(){
        String appName = Foundation.app().getAppId();
        System.out.println("Current application name: " + appName);
    }

}

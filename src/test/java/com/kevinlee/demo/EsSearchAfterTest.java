package com.kevinlee.demo;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @ClassName EsSearchAfterTest
 * @Author kevinlee
 * @Date 2022/2/24 11:26
 * @Version 1.0
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsSearchAfterTest {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void testSearchAfter() throws IOException {
        Object[] objects = new Object[]{};
        try {
            for (int i = 0; i < 4; i++) {
                SearchRequest searchRequest = new SearchRequest("users");
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.size(1);
                searchSourceBuilder.sort("age", SortOrder.DESC);
                if(objects.length>0) {
                    searchSourceBuilder.searchAfter(objects);
                }
                searchRequest.source(searchSourceBuilder);
                System.out.println(searchRequest.source().toString());
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                System.out.println(searchResponse);
                SearchHit[] hits = searchResponse.getHits().getHits();
                objects = hits[hits.length-1].getSortValues();
                log.info("searchAfter={}",objects);
                log.info("hits={}",hits);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

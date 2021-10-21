package com.kevinlee.demo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Es {

    @Resource
    RestHighLevelClient restHighLevelClient;

    /**
     * 判断索引是否存在
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        final GetIndexRequest indexRequest = new GetIndexRequest("movies");
        final boolean exists = restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 插入或更新索引值
     */
    @Test
    public void insertOrUpdateOne() {
        IndexRequest request = new IndexRequest("movies");
        Movie movie = new Movie();
        movie.setId("49272");
        movie.setTitle("kevin test");
        request.id(movie.getId());
        request.source(JSONObject.toJSONString(movie), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Data
    class Movie {

        private String id;

        private String title;

        private Integer year;
    }

}

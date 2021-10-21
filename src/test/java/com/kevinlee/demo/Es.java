package com.kevinlee.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
@Slf4j
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
     * 插入或更新索引值(会将没有传入的字段给删除)
     */
    @Test
    public void insertOrUpdateOne() {
        IndexRequest request = new IndexRequest("movies");
        Movie movie = new Movie();
        movie.setId("49273");
        movie.setTitle("kevin test");
        movie.setYear(2018);
        request.id(movie.getId());
        request.source(JSONObject.toJSONString(movie), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void getIndex() {
        GetRequest getRequest = new GetRequest("movies","49272");

        try {
            GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            Map<String, Object> source = documentFields.getSource();
            System.out.println(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void getIndexById(){
        GetRequest getRequest = new GetRequest("movies").id("49272");
        Movie userEntity = null;
        try {
            GetResponse getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
            Map<String,Object> map = getResponse.getSource();
            userEntity = BeanUtil.mapToBean(map,Movie.class,false, CopyOptions.create());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * match查询
     */
    @Test
    public  void testMatch() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("movies");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title.keyword", "kevin test");
        //忽略格式错误
        matchQueryBuilder.lenient();
        searchSourceBuilder.query(matchQueryBuilder);

        //组装语句
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //获取到结果
        SearchHits hits = response.getHits();
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            log.info("输出数据:" + iterator.next().getSourceAsString());
        }
    }


    @Data
    class Movie {

        private String id;

        private String title;

        private Integer year;
    }

}

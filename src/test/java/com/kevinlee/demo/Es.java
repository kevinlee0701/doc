package com.kevinlee.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class Es {

    @Resource
    RestHighLevelClient restHighLevelClient;

    String index ="movies";
    String testId="49273";

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



    /**
     * 局部更新
     * @throws IOException
     */
    @Test
    public void update2() throws IOException {
        UpdateByQueryRequest updateByQuery  = new UpdateByQueryRequest(index);
        updateByQuery.setConflicts("proceed");
        MatchQueryBuilder query = QueryBuilders.matchQuery("title.keyword", "kevin test");
        updateByQuery.setQuery(query);
        updateByQuery.setScript(new Script("ctx._source['title']='test kevin'"));
        try {
            BulkByScrollResponse response = restHighLevelClient.
                    updateByQuery(updateByQuery,RequestOptions.DEFAULT);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 局部更新
     * @throws IOException
     */
    @Test
    public void update3() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index,testId);
        Movie params = new Movie();
        params.setYear(2011);
        updateRequest.doc(JSONObject.toJSONString(params),XContentType.JSON);
        restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);

    }

    /**
     * 条件更新
     *
     * @return
     */
    @Test
    public  void updateQuery() {
        QueryBuilder queryBuilder =QueryBuilders.matchQuery("title.keyword", "lifeng");
        Map<String,Object> updateParams = new HashMap<>();
        ArrayList<Integer> integers = Lists.newArrayList(1, 3, 4,5);
        updateParams.put("list",integers);
        updateParams.put("title","lifeng2");
        UpdateByQueryRequest request = updateByQuery(index, queryBuilder, updateParams);


        try {
            BulkByScrollResponse response = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
        }
    }


    public static UpdateByQueryRequest updateByQuery(String index, QueryBuilder query, Map<String, Object> document) {
        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(index);
        updateByQueryRequest.setQuery(query);
        StringBuilder script = new StringBuilder();
        Set<String> keys = document.keySet();
        for (String key : keys) {
            String appendValue = "";
            Object value = document.get(key);
            if (value instanceof Number) {
                appendValue = value.toString();
            } else if (value instanceof String) {
                appendValue = "'" + value.toString() + "'";
            } else if (value instanceof List){
                appendValue = JSONObject.toJSONString(value);
            } else {
                appendValue = value.toString();
            }
            script.append("ctx._source.").append(key).append("=").append(appendValue).append(";");
        }
        log.info("script={}",script.toString());
        updateByQueryRequest.setScript(new Script(script.toString()));
        return updateByQueryRequest;
    }



    @Data
    class Movie {

        private String id;


        private String title;

        private Integer year;
    }

}

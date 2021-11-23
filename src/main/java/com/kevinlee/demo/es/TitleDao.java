package com.kevinlee.demo.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitleDao  extends ElasticsearchRepository<Title,String> {
    /**
     * @description: 根据body内容查询
     * @Author kevinlee
     * @Date  2021/11/23
     **/
    List<Title> findByBody(String body);
}

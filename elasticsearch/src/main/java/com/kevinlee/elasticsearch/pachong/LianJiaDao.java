package com.kevinlee.elasticsearch.pachong;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 龙华园索引
 * @Author kevinlee
 * @Date  2021/11/22
 **/
@Repository
public interface LianJiaDao extends ElasticsearchRepository<LianJia,String> {

    List<LianJia> findLianJiaByAddress(String address);
}

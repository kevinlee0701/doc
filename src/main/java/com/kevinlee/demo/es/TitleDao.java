package com.kevinlee.demo.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleDao  extends ElasticsearchRepository<Title,String> {
}

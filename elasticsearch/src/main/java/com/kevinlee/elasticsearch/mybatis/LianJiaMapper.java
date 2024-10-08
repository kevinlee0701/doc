package com.kevinlee.elasticsearch.mybatis;


import com.kevinlee.elasticsearch.pachong.LianJia;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LianJiaMapper {
    List<Lhy> findAll();


    void insert(Lhy lhy);
}

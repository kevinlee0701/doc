package com.kevinlee;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName RedisBean
 * @Author kevinlee
 * @Date 2021/12/27 16:59
 * @Version 1.0
 **/
@Data
public class RedisBean implements Serializable  {

    private Integer id;

    private String name;

    private List list;
}

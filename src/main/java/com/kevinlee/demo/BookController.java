package com.kevinlee.demo;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class BookController {
    @Resource
    private RedisTemplate redisTemplate;

    @RequestMapping("/")
    public String index(){
        return "/index";
    }
}

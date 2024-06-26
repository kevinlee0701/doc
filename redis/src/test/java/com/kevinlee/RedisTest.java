package com.kevinlee;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    private static final String GOODS_CACHE = "seckill:goodsStock:";

    private String  id="kevin";

    /**
     * @author binghe
     * @description 秒杀前构建商品缓存代码示例
     */
    @Test
    public void test(){
        String key = getCacheKey(id);
        Map<String, Integer> goods = new HashMap<>();
        goods.put("Total", 100);
        goods.put("initStatus", 0);
        goods.put("Booked", 0);
        redisTemplate.opsForHash().putAll(key, goods);
    }
    @Test
    public void test23(){
        String dateTimeStr = "2022-08-05";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, df);
        Date from = Date.from(dateTime.toInstant(ZoneOffset.of("+8")));
        System.out.println(from);
    }

    @Test
    public void test3(){
        List list = CacheUtil.getCache("list", List.class);
        if(list == null){
            list = new ArrayList();
        }
        list.add("lifeng");
        CacheUtil.addCache("list",list);
        System.out.println(CacheUtil.getCache("list",List.class));
    }

    /**
     * 秒杀测试
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        String key = getCacheKey(id);
        // 执行 lua 脚本
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>();
        // 指定 lua 脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis-lua.lua")));
        // 指定返回类型
        redisScript.setResultType(Integer.class);

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                Object seckillCount =redisTemplate.execute(redisScript, Arrays.asList(key), 1);
                log.info("result={},name={},time={}",seckillCount,Thread.currentThread(),System.currentTimeMillis());
            }
        };


        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(10);
        for(int i=0;i<100;i++){
            cachedThreadPool.execute(r1);
        }

        Thread.sleep(10000);

    }


    private String getCacheKey(String id) {
        return  GOODS_CACHE.concat(id);
    }
}

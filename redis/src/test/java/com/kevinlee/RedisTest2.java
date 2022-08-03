package com.kevinlee;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest2 {

    @Resource
    private RedisTemplate redisTemplate;

    private static final String GOODS_CACHE = "seckill:goodsStock:";

    private String id = "kevin";

    /**
     * @author binghe
     * @description 秒杀前构建商品缓存代码示例
     */
    @Test
    public void test() {
        ListOperations<String, List<Object>> listOperations = redisTemplate.opsForList();
        List<Object> list = new CopyOnWriteArrayList<>();
        list.add(1);
        listOperations.leftPush("test", list);
        List<Object> test = listOperations.leftPop("test");
        System.out.println(test instanceof CopyOnWriteArrayList);
        System.out.println(test instanceof List);
    }
    @Test
    public void test6() {
        FixSizeLinkedListCache list = new FixSizeLinkedListCache<>();
        list.setCapacity(2);
        list.setLinkedList(new LinkedList<>());
        list.add("test1");
        CacheUtil.addCache("testtest",list);
        FixSizeLinkedListCache testtest = CacheUtil.getCache("testtest", FixSizeLinkedListCache.class);
        System.out.println(testtest.toString());

    }

    @Test
    public void test2() {
        HashOperations<String, Object, Object> redis = redisTemplate.opsForHash();
        List<Object> list = new CopyOnWriteArrayList<>();
        list.add(1);
        redis.put("test2", "name", "name-value");
        redis.put("test2", "age", "age-value");
        redis.put("test2", "city", "city-value");
    }

    @Test
    public void test3() {
        RedisBean user = new RedisBean();
        user.setId(1);
        user.setName("kevin");
        user.setList(Lists.newCopyOnWriteArrayList());
        redisTemplate.opsForValue().set(user.getId() + "-RedisBean", user);
        RedisBean o = (RedisBean) redisTemplate.opsForValue().get(user.getId() + "-RedisBean");
        System.out.println(o.getList() instanceof CopyOnWriteArrayList);

    }

    @Data
    class User {
        private Integer id;

        private String name;

        private List list;

        @Override
        public String toString() {
            return "User{" + "id=" + id + ", name='" + name + '\'' + ", list=" + list + '}';
        }
    }
}

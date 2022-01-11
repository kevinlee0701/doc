package com.kevinlee;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * @ClassName CacheUtil
 * @Author kevinlee
 * @Date 2022/1/10 18:14
 * @Version 1.0
 **/
public class CacheUtil {

    public static final String REDIS_CLIENT_BEAN = "redisTemplate";
    private static RedisTemplate redisTemplate = SpringContextUtils.getBean(REDIS_CLIENT_BEAN, RedisTemplate.class);
    /**
     * 写入缓存
     * @param key
     * @param ot
     */
    @SuppressWarnings("unchecked")
    public static <T> void addCache(String key,T ot){

        redisTemplate.opsForValue().set(key,ot);

    }
    /**
     * 获取缓存
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static <T> T getCache(String key,Class<T> classes){
        return (T)redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     * @param key
     */
    @SuppressWarnings("unchecked")
    public static void delCache(String key){
        redisTemplate.delete(key);
    }
}



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {
    /*
     * redis缓存中的key
     */
    String key();
    /*
     * 缓存时间，默认缓存时间是5分钟 60*5
     */
    long expire() default 300L;
}

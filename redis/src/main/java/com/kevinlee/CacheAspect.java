
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

/**
 * @Author xiakai
 * @Date 2023/12/28 16:58
 */
@Aspect
@Component
@Slf4j
public class CacheAspect {

    @Autowired
    private RedisUtils redisUtils;

    static final String SEPARATOR = "_";

    static final String SYSTEM = "KOO_JIAOFU_VIP_SERVICE_%s_%s";


    @Around(value = "@annotation(cache)", argNames = "point,cache")
    public Object doAroundAdvice(ProceedingJoinPoint point, RedisCache cache) {
        log.info("进入缓存切面：{}", point.getSignature().toString());
        Object proceed = null;
        try {
            String key = formatKey(point, cache);
            if (redisUtils.exists(key)) {
                return redisUtils.get(key);
            }
            proceed = point.proceed();
            redisUtils.setEx(key, proceed, cache.expire());
        } catch (Throwable e) {
            log.info("缓存切面异常：", e);
        }
        return proceed;

    }

    /**
     * 格式化redis的key
     */
    public String formatKey(ProceedingJoinPoint point, RedisCache cache) {
        Object[] args = point.getArgs();
        StringBuilder stringBuilder = new StringBuilder(cache.key());
        if (args.length != 0) {
            stringBuilder.append(SEPARATOR);
            for (int i = 0; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i != args.length - 1) {
                    stringBuilder.append(SEPARATOR);
                }
            }
        }
        return String.format(SYSTEM, cache.key(), DigestUtils.md5DigestAsHex(stringBuilder.toString().getBytes()));
    }
}

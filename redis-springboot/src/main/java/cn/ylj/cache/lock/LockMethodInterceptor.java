package cn.ylj.cache.lock;

import cn.ylj.cache.IRedisService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//该注解表示 该类作为切面通知类 供容器读取
@Aspect
@Configuration
public class LockMethodInterceptor {

    public LockMethodInterceptor(IRedisService redisLockHelper, CacheKeyGenerator cacheKeyGenerator) {
        this.redisLockHelper = redisLockHelper;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    private final IRedisService redisLockHelper;
    private final CacheKeyGenerator cacheKeyGenerator;


    //环绕增强 切点表达式
    @Around("execution(public * *(..)) && @annotation(cn.ylj.cache.lock.CacheLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        if (StringUtils.isEmpty(lock.prefix())) {
            throw new RuntimeException("lock key don't null...");
        }
        final String lockKey = cacheKeyGenerator.getLockKey(pjp, false);
        final String cacheKey = cacheKeyGenerator.getLockKey(pjp, true);
        if (lock.cacheResultData()) {
            String cacheValue = redisLockHelper.getString(cacheKey);
            if (!StringUtils.isEmpty(cacheValue)) {
                Object result = JSONObject.parseObject(cacheValue, method.getReturnType());
                return result;
            }
        }
        String value = UUID.randomUUID().toString();
        try {
            // 假设上锁成功，但是设置过期时间失效，以后拿到的都是 false
            final boolean success = redisLockHelper.lock(lockKey, value, lock.expire(), lock.timeUnit());
            if (!success) {
                Long expire = redisLockHelper.getExpire(lockKey);
                //-1为无过期  -2为不存在
                if(expire == -1){
                    redisLockHelper.unlock(lockKey, value);
                }
                throw new RuntimeException("业务逻辑错误");
            }
            try {
//                return pjp.proceed();
                Object obj = pjp.proceed();
                if (lock.cacheResultData()) {
                    ObjectMapper objectMapper = PcObjectMapper.objectMapperForSerializable();
                    String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
                    redisLockHelper.setString(cacheKey, jsonResult, lock.expire(), TimeUnit.SECONDS);
                }

                return obj;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            } finally {
                redisLockHelper.unlock(lockKey, value);
            }
        } finally {
//            redisLockHelper.unlock(lockKey, value);
        }
    }
}
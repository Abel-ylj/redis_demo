package cn.ylj.cache.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * key生成器 实现
 */
@Service
public class LockKeyGenerator implements CacheKeyGenerator {
    private String redisLockPrefix = "ylj:lock:";
    private String redisCachePrefix = "ylj:cache:";

    @Override
    public String getLockKey(ProceedingJoinPoint pjp, boolean isCache) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        CacheLock lockAnnotation = method.getAnnotation(CacheLock.class);
        final Object[] args = pjp.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        // TODO 默认解析方法里面带 CacheParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(lockAnnotation.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) { //切点方法 参数中没有带C
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final CacheParam annotation = field.getAnnotation(CacheParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(lockAnnotation.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        String cacheParamsStr = builder.toString();
//        if (cacheParamsStr.length() > 50) {
//            //参数实体类时 key太长了，转成MD5
//            cacheParamsStr = MD5Util.MD5Encode(cacheParamsStr, "UTF-8");
//        }
        return (isCache ? redisCachePrefix : redisLockPrefix) + lockAnnotation.prefix() + cacheParamsStr;
    }
}
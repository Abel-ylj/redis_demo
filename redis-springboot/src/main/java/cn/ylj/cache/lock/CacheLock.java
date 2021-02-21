package cn.ylj.cache.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {

    /**
     * redis 锁key的前缀
     *
     * @return redis 锁key的前缀
     */
    String prefix() default "";

    /**
     * 过期秒数,默认为5秒
     *
     * @return 自旋乐观锁的时间
     */
    int expire() default 5;

    /**
     * 超时时间单位
     *
     * @return 秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * <p>Key的分隔符（默认 :）</p>
     * <p>生成的Key：N:SO1008:500</p>
     *
     * @return String
     */
    String delimiter() default ":";

    /**
     * 是否缓存接口返回结果
     * @return
     */
    boolean cacheResultData() default false;
}
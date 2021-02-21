package cn.ylj.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public interface IRedisService {

    /**
     * Gets key.
     *
     * @param key the key
     * @return the key
     */
    String getString(String key);

    String getHash(String key, String skey);

    Map getHash(String key);
    /**
     * Delete key.
     *
     * @param key the key
     */
    void deleteString(String key);

    /**
     * Sets key.
     *
     * @param key   the key
     * @param value the value
     */
    void setString(String key, String value);
    /**
     * Sets key.
     *
     * @param key     the key
     * @param value   the value
     * @param timeout the timeout
     * @param unit    the unit
     */
    void setString(String key, String value, final long timeout, final TimeUnit unit);

    /**
     * 设置hash类型
     * @param key
     * @param skey
     * @param value
     * @param timeout
     * @param unit
     */
    void setHash(String key, String skey, String value, long timeout, TimeUnit unit);

    /**
     * 设置键过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     */
    void expireKey(String key, final long timeout, final TimeUnit unit);


    /**
     * 检查key是否存在，返回boolean值
     *
     * @param key
     * @return
     */
    boolean hasKey(String key);

    /**
     * keys key.
     *
     * @param key the key
     * @return result
     */
    Set<String> keys(String key);

    /**
     * 自增+1
     *
     * @param key
     */
    void increment(String key);

    /**
     * 自减 -1
     *
     * @param key
     */
    void decrease(String key);

    /**
     * 获取锁（存在死锁风险）
     *
     * @param lockKey lockKey
     * @param value   value
     * @param time    超时时间
     * @param unit    过期单位
     * @return
     */
    boolean tryLock(final String lockKey, String value, long time, TimeUnit unit);

    /**
     * 获取锁
     *
     * @param lockKey lockKey
     * @param uuid    UUID
     * @param timeout 超时时间
     * @param unit    过期单位
     * @return
     */
    boolean lock(String lockKey, final String uuid, long timeout, final TimeUnit unit);

    /**
     * 解锁
     *
     * @param lockKey
     * @param value
     */
    void unlock(String lockKey, String value);

    /**
     * 延迟unlock
     *
     * @param lockKey   lockKey
     * @param uuid      client(最好是唯一键的)
     * @param delayTime 延迟时间
     * @param unit      时间单位
     */
    void unlock(final String lockKey, final String uuid, long delayTime, TimeUnit unit);

    /**
     * 获取过期时间
     * @param lockKey
     * @return
     */
    Long getExpire(final String lockKey);
}

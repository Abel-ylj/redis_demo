package cn.ylj.cache;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
@SuppressWarnings("unchecked")
public class RedisServiceImpl implements IRedisService {
    @Resource
    private StringRedisTemplate rt;

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    private static final String DELIMITER = "|";

    /**
     * @param key the key
     * @return
     */
    @Override
    public String getString(String key) {
        String value = null;
        ValueOperations<String, String> ops = rt.opsForValue();
        if (rt.hasKey(key)) {
            value = ops.get(key);
        }
        return value;
    }

    /**
     * @param key the key
     */
    @Override
    public void deleteString(String key) {
        rt.delete(key);
    }

    @Override
    public void setString(String key, String value) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");

        ValueOperations<String, String> ops = rt.opsForValue();
        ops.set(key, value);

    }

    @Override
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");
        Preconditions.checkArgument(unit != null, "TimeUnit is not null");
        ValueOperations<String, String> ops = rt.opsForValue();
        try {
            ops.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rt.expire(key, timeout, unit);
    }

    public void setHash(String key, String skey, String value, long timeout, TimeUnit unit) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");
        Preconditions.checkArgument(unit != null, "TimeUnit is not null");
        HashOperations<String, String, String> ops = rt.opsForHash();
        try {
            ops.putIfAbsent(key, skey, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rt.expire(key, timeout, unit);
    }

    public String getHash(String key, String skey){
        HashOperations<String, String, String> hash = rt.opsForHash();
        String s = hash.get(key, skey);
        return s;
    }

    @Override
    public Map getHash(String key) {
        Map<Object, Object> map = rt.opsForHash().entries(key);
        return map;
    }

    /**
     * 设置键过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     */
    public void expireKey(String key, final long timeout, final TimeUnit unit) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");
        Preconditions.checkArgument(unit != null, "TimeUnit is not null");
        rt.expire(key, timeout, unit);
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    @Override
    public boolean hasKey(String key) {
        return rt.hasKey(key);
    }

    /**
     * @param key the key
     * @return
     */
    @Override
    public Set<String> keys(String key) {
        Set<String> keys = rt.keys(key + "*");
        if (keys != null && keys.size() != 0) {
            return keys;
        }
        return null;
    }

    @Override
    public void increment(String key) {
        if (hasKey(key)) {
            rt.boundValueOps(key).increment(1);
        }
    }

    @Override
    public void decrease(String key) {
        if (hasKey(key)) {
            rt.boundValueOps(key).increment(-1);
        }
    }

    /**
     * 获取锁（存在死锁风险）
     *
     * @param lockKey lockKey
     * @param value   value
     * @param time    超时时间
     * @param unit    过期单位
     * @return
     */
    @Override
    public boolean tryLock(String lockKey, String value, long time, TimeUnit unit) {
        return rt.execute(
                (RedisCallback<Boolean>) connection ->
                        connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, unit), RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
    }

    /**
     * 获取锁
     *
     * @param lockKey lockKey
     * @param uuid    UUID
     * @param timeout 超时时间
     * @param unit    过期单位
     * @return
     */
    @Override
    public boolean lock(String lockKey, String uuid, long timeout, TimeUnit unit) {
        final long milliseconds = Expiration.from(timeout, unit).getExpirationTimeInMilliseconds();
        boolean success = rt.opsForValue().setIfAbsent(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
        if (success) {
            rt.expire(lockKey, timeout, TimeUnit.SECONDS);
        } else {
            String oldVal = rt.opsForValue().getAndSet(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
            if (oldVal == null) {
                return true;
            }
            final String[] oldValues = oldVal.split(Pattern.quote(DELIMITER));
            if (Long.parseLong(oldValues[0]) + 1 <= System.currentTimeMillis()) {
                return true;
            }
        }
        return success;
    }

    /**
     * 解锁
     *
     * @param lockKey
     * @param value
     */
    @Override
    public void unlock(String lockKey, String value) {
        unlock(lockKey, value, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟解锁
     *
     * @param lockKey   lockKey
     * @param uuid      client(最好是唯一键的)
     * @param delayTime 延迟时间
     * @param unit      时间单位
     */
    @Override
    public void unlock(String lockKey, String uuid, long delayTime, TimeUnit unit) {
        if (StringUtils.isEmpty(lockKey)) {
            return;
        }
        if (delayTime <= 0) {
            doUnlock(lockKey, uuid);
        } else {
            EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey, uuid), delayTime, unit);
        }
    }

    /**
     * @param lockKey key
     * @param uuid    client(最好是唯一键的) 创建用户钱包地址失败
     */
    private void doUnlock(final String lockKey, final String uuid) {
        String val = rt.opsForValue().get(lockKey);
        if (null == val) {
            return;
        }
        final String[] values = val.split(Pattern.quote(DELIMITER));
        if (values.length <= 0) {
            return;
        }
        if (uuid.equals(values[1])) {
            rt.delete(lockKey);
        }
    }

    @Override
    public Long getExpire(final String lockKey) {
        return rt.getExpire(lockKey);
    }
}

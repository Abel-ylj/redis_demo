package cn.ylj;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class TestHash {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.53.107", 6379);
        jedis.auth("Yang123456.");
        jedis.flushDB();

        System.out.println("====hash添加====");
        Map<String, String> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        map.put("k3", "v3");
        jedis.hmset("hash", map);
        jedis.hset("hash","k4","v4");

        System.out.println("====hash删除小kv====");
        System.out.println("hash的删除前:" + jedis.hgetAll("hash"));
        jedis.hdel("hash","k1");
        System.out.println("hash的删除后:" + jedis.hgetAll("hash"));

        System.out.println("====hash查询====");
        System.out.println("查询kv数量:" + jedis.hlen("hash"));
        System.out.println("查询key是否存在:" + jedis.hexists("hash", "k2"));
        System.out.println("查询key是否存在:" + jedis.hexists("hash", "k1"));
        System.out.println("查询v:" + jedis.hmget("hash","k3"));
        System.out.println("查询v:" + jedis.hmget("hash","k3","k4"));

        System.out.println("====hash遍历====");
        System.out.println("hash的所有的键值对为:" + jedis.hgetAll("hash"));
        System.out.println("hash 小键集合:" + jedis.hkeys("hash"));
        System.out.println("hash 小值集合:" + jedis.hvals("hash"));

        System.out.println("====小kv原子加减====");
        System.out.println("不存在则添加，存在则执行:" + jedis.hincrBy("h666", "hk1", 10));
        System.out.println(jedis.hgetAll("h666"));
        System.out.println("不存在则添加，存在则执行:" + jedis.hincrBy("h666", "hk1", 10));
        System.out.println(jedis.hgetAll("h666"));

    }
}
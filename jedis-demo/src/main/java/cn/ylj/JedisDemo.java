package cn.ylj;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class JedisDemo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.53.107",6379);
        jedis.auth("Yang123456.");

        System.out.println("清空数据：" + jedis.flushDB());
        System.out.println("判断值是否存在:" + jedis.exists("username"));
        System.out.println("新增k-v:" + jedis.set("username", "ylj"));
        System.out.println("新增k-v:" + jedis.set("pwd","123"));
        System.out.println("查询所有的keys");
        Set<String> keys = jedis.keys("*");
        System.out.println(keys);
        System.out.println("删除k-v pwd-ylj:" + jedis.del("pwd"));
        System.out.println("判断pwd是否存在:" + jedis.exists("pwd"));
        System.out.println("查询key对应的value类型：" + jedis.type("username"));
        System.out.println("随机key:" + jedis.randomKey());
        System.out.println("重命名key:" + jedis.rename("username", "newname"));
        System.out.println("重命名key的查询:" + jedis.get("newname"));
        System.out.println("选择数据库:" + jedis.select(1));
        System.out.println("查询当前库中key的数量:" + jedis.dbSize());
        System.out.println("删除所有数据库中的所有key" + jedis.flushAll());
    }
}
package cn.ylj;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class TestString {

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("121.4.53.107", 6379);
        jedis.auth("Yang123456.");
        jedis.flushDB();

        System.out.println("======增加数据=====");
        System.out.println(jedis.set("k1", "v1"));
        System.out.println(jedis.set("k2", "v2"));
        System.out.println(jedis.set("k3", "v3"));
        System.out.println("删除k2:" + jedis.del("k2"));
        System.out.println("获取k2:" + jedis.get("k2"));
        System.out.println("修改k1:" + jedis.set("k1", "modifyValue"));
        System.out.println("获取k1:" + jedis.get("k1"));
        System.out.println("字符串append:" + jedis.append("k3", ",append value"));
        System.out.println("获取k3:" + jedis.get("k3"));
        System.out.println("mset:" + jedis.mset("k01","v01","k02","v02","k03","v03"));
        System.out.println("mget:" + jedis.mget("k01","k02", "k03"));


        jedis.flushDB();
        System.out.println("=====幂等增加(分布式锁,添加的过程就是尝试获取锁的过程)=====");
        System.out.println(jedis.setnx("lock1", "lck01"));
        System.out.println(jedis.setnx("lock2", "lck02"));
        System.out.println(jedis.setnx("lock2", "lck02-new"));
        System.out.println(jedis.mget("lock1","lock2"));

        jedis.flushDB();
        System.out.println("=====expire有效期=====");
        System.out.println(jedis.setex("livingAnimal", 2, "value3"));
        System.out.println(jedis.get("livingAnimal"));
        TimeUnit.SECONDS.sleep(3);
        System.out.println(jedis.get("livingAnimal"));

        jedis.flushDB();
        System.out.println("=====getset命令======");
        System.out.println(jedis.set("kkk", "vvvv"));
        System.out.println(jedis.getSet("kkk","nvnvnv"));

        System.out.println("获取kkk的value字串的subString：" + jedis.getrange("kkk", 1,2 ));
    }
}
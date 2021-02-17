package cn.ylj;

import redis.clients.jedis.Jedis;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class TestSet {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.53.107", 6379);
        jedis.auth("Yang123456.");
        jedis.flushDB();

        System.out.println("====向集合中添加元素====");
        System.out.println(jedis.sadd("ss", "m6", "m5", "m4", "m3", "m2", "m1", "m5", "m5"));
        System.out.println("set所有成员:" + jedis.smembers("ss"));
        System.out.println("删除1个成员:" + jedis.srem("ss", "m5"));
        System.out.println("set所有成员:" + jedis.smembers("ss"));

        System.out.println("====向集合中删除元素====");
        System.out.println("随机移除set中的1个成员:" + jedis.spop("ss"));
        System.out.println("set所有成员:" + jedis.smembers("ss"));

        System.out.println("====元素判断====");
        System.out.println("集合成员个数:" + jedis.scard("ss"));
        System.out.println("判断指定成员是否在集合中:" + jedis.sismember("ss", "m5"));
        System.out.println("判断指定成员是否在集合中:" + jedis.sismember("ss", "m4"));

        System.out.println("====元素在集合间移动====");
        System.out.println(jedis.sadd("s1", "e1", "e2", "e3", "e4", "e5"));
        System.out.println("成员e1,从s1-->s3:" + jedis.smove("s1", "s3", "e1"));
        System.out.println("查看集合s1:" + jedis.smembers("s1"));
        System.out.println("查看集合s3:" + jedis.smembers("s3"));
        jedis.flushDB();

        System.out.println("====集合运算:交 并 差====");
        System.out.println(jedis.sadd("s1", "e1", "e2", "e3", "e4", "e5"));
        System.out.println(jedis.sadd("s2", "e4", "e5", "e6", "e7", "e8"));
        System.out.println("查看集合s1:" + jedis.smembers("s1"));
        System.out.println("查看集合s2:" + jedis.smembers("s2"));
        System.out.println("s1 交 s2:" + jedis.sinter("s1", "s2"));
        System.out.println("s1 并 s2:" + jedis.sunion("s1", "s2"));
        System.out.println("s1 差 s2:" + jedis.sdiff("s1", "s2"));

        //运算结果存入新集合
        System.out.println("s1 交 s2:" + jedis.sinterstore("sinter", "s1", "s2"));
        System.out.println("sinter:" + jedis.smembers("sinter"));
        System.out.println("s1 并 s2:" + jedis.sunionstore("sunion", "s1", "s2"));
        System.out.println("sunion:" + jedis.smembers("sunion"));
        System.out.println("s1 差 s2:" + jedis.sdiffstore("sdiff", "s1", "s2"));
        System.out.println("sdiff:" + jedis.smembers("sdiff"));
    }
}
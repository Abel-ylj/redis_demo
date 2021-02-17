package cn.ylj;

import redis.clients.jedis.Jedis;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class TestList {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.53.107", 6379);
        jedis.auth("Yang123456.");
        jedis.flushDB();

        System.out.println("====添加list====");
        jedis.lpush("cc", "v1", "v2", "v3", "v4");
        jedis.lpush("cc", "v5");
        jedis.lpush("cc", "v5");
        jedis.lpush("cc", "v5");
        jedis.lpush("cc", "v6");
        System.out.println("list遍历:" + jedis.lrange("cc", 0, -1));

        //链表结构的操作
        System.out.println("删除指定value，且指定个数:" + jedis.lrem("cc", 2, "v5"));
        System.out.println("修改指定索引处的值:" + jedis.lset("cc", 1, "vvv1"));
        System.out.println("查询指定索引处的值:" + jedis.lindex("cc",1));
        System.out.println("list遍历:" + jedis.lrange("cc", 0, -1));
        System.out.println("list遍历,区间0-3元素:" + jedis.lrange("cc", 0, 3));
        System.out.println("长度:" + jedis.llen("cc"));

        //栈操作
        System.out.println("====栈操作====");
        System.out.println("左pop:" + jedis.lpop("cc"));
        System.out.println("list遍历:" + jedis.lrange("cc", 0, -1));
        System.out.println("右pop:" + jedis.rpop("cc"));
        System.out.println("list遍历:" + jedis.lrange("cc", 0, -1));

        //排序
        jedis.flushDB();
        System.out.println("====排序====");
        System.out.println("右pop:" + jedis.rpush("cc","1","3","5","7","2","4","6"));
        System.out.println("排序:" + jedis.sort("cc"));
    }
}
package cn.ylj.redisspringboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;

import java.util.List;

/**
 * spring-data-redis 集成redis操作， 网络连接使用基于netty的letuce
 */
@SpringBootTest
class RedisSpringbootApplicationTests {

    //redisTemplate底层是lettuce netty nio
    @Autowired
    private RedisTemplate redisTemplate;

    //String bitmap
    @Test
    void stringTest(){
//        redisTemplate.opsForValue().set("k1", "v1");
//        String v1 = (String)redisTemplate.opsForValue().get("k1");
//        System.out.println(v1);

        redisTemplate.opsForValue().setBit("bitmap", 0, true);
        redisTemplate.opsForValue().setBit("bitmap", 1, false);
        redisTemplate.opsForValue().setBit("bitmap", 2, true);
        redisTemplate.opsForValue().setBit("bitmap", 3, false);

        Boolean bitmap0 = redisTemplate.opsForValue().getBit("bitmap", 2);
        Boolean bitmap1 = redisTemplate.opsForValue().getBit("bitmap", 3);
        System.out.println(bitmap0 + "," + bitmap1);
    }

    //list
    @Test
    void listTest(){
        ListOperations lst = redisTemplate.opsForList();
        lst.leftPush("lst", "item1");
        lst.leftPush("lst", "item2");
        lst.leftPush("lst", "item3");
        lst.leftPush("lst", "item4");
        List rlst = lst.range("lst", 0, -1);
        System.out.println(rlst);
    }

    //其余数据类型操作
    @Test
    void setTest(){
//        SetOperations set = redisTemplate.opsForSet();
        ZSetOperations zset = redisTemplate.opsForZSet();
//        HashOperations hash = redisTemplate.opsForHash();
//        GeoOperations geo = redisTemplate.opsForGeo();
//        HyperLogLogOperations hyperLogLogOperations = redisTemplate.opsForHyperLogLog(); //基数统计 网站UV Unique View
    }

    //获取redis的连接对象
    @Test
    void conncetionTest(){
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.flushDb();
        connection.flushAll();
    }


    @Test
    void contextLoads() {
    }

}

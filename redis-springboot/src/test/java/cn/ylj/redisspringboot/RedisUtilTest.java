package cn.ylj.redisspringboot;

import cn.ylj.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author : ylj
 * create at:  2021/2/18
 */
@SpringBootTest
public class RedisUtilTest {

    @Autowired
    RedisUtil redisUtil;

    @Test
    public void stringTest(){
        redisUtil.set("classmate", "ylj");
        System.out.println(redisUtil.get("classmate"));
    }
}
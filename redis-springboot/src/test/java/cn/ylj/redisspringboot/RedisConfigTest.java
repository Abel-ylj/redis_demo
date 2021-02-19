package cn.ylj.redisspringboot;

import cn.ylj.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author : ylj
 * create at:  2021/2/18
 */
@SpringBootTest
public class RedisConfigTest {

    //redisTemplate底层是lettuce netty nio
    @Autowired
    private RedisTemplate redisTemplate;

    //基本读写测试
    @Test
    public void redisTemplateTest(){
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        connection.flushDb();
        User user = new User();
        user.setUsername("周杰伦");
        user.setAge(18);
        redisTemplate.opsForValue().set("user", user);

        Object user2 = redisTemplate.opsForValue().get("user");
        System.out.println((User) user2);
    }


}
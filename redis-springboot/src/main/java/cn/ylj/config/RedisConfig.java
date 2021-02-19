package cn.ylj.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
@Configuration
public class RedisConfig {

//    @Value("${spring.redis.sentinel.nodes}")
//    private String redisNodes;
//
//    @Value("${spring.redis.sentinel.master}")
//    private String master;

    //自定义redisTemplate
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key 使用String序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //hash 的小key也是String
        template.setHashKeySerializer(stringRedisSerializer);

        //value 使用json
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //hash 的小value也是json
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

//    @Bean
//    public RedisSentinelConfiguration redisSentinelConfiguration(){
//        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
//        String[] host = redisNodes.split(",");
//        for(String redisHost : host){
//            String[] item = redisHost.split(":");
//            String ip = item[0];
//            String port = item[1];
//            configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
//        }
//        configuration.setMaster(master);
//        return configuration;
//    }
}
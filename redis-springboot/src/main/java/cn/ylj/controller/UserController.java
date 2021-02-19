package cn.ylj.controller;

import cn.ylj.pojo.User;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author : ylj
 * create at:  2021/2/19
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    RedisTemplate<String, Object> redisTemplate; //序列化器会自动将 key 和 value序列化为json

    @RequestMapping(value = "/add/{age}/{username}",method = RequestMethod.GET)
    public String add(@PathVariable("username") String username, @PathVariable("age") int age){
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        redisTemplate.opsForValue().set("user", user);
        return "ok";
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public Object get(){
        return redisTemplate.opsForValue().get("user");
    }
}
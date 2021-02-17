package cn.ylj.tx;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * @author : ylj
 * create at:  2021/2/17
 */
public class TestTx {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("121.4.53.107", 6379);
        jedis.auth("Yang123456.");
        jedis.flushDB();

        //开启事务
        Transaction multi = jedis.multi();
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "ylj");
            int i = 1/0;
            jsonObject.put("pwd", "123456");
            String jsonString = jsonObject.toJSONString();
            multi.set("user1", jsonString);     //提交命令到队列
            multi.exec();    //正常则执行
        } catch (Exception e) {
            multi.discard(); //异常则取消事务
            e.printStackTrace();
        } finally {
            jedis.close();   //关闭连接
        }
    }
}
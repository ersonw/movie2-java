package com.telebott.movie2java.dao;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.UserShareCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ShortDao {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final Timer timer = new Timer();
    public void pushData(UserShareCode data){
        UserShareCode object = findById(data.getId());
        if (object != null){
            pop(object);
        }
        redisTemplate.opsForSet().add("data", JSONObject.toJSONString(data));
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                popOrder(object);
//            }
//        }, 1000 * 60 * 30);
    }
    public UserShareCode findById(long Id){
        Set orders = redisTemplate.opsForSet().members("data");
        if (orders != null){
            for (Object o: orders) {
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                if (Id == jsonObject.getLong("id")){
                    return JSONObject.toJavaObject(jsonObject,UserShareCode.class);
                }
            }
        }
        return null;
    }
    public UserShareCode findByCode(String code){
        Set orders = redisTemplate.opsForSet().members("data");
        if (orders != null){
            for (Object o: orders) {
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                if (code.equals(jsonObject.getString("inviteCode"))) {
                    return JSONObject.toJavaObject(jsonObject,UserShareCode.class);
                }
            }
        }
        return null;
    }
    public void pop(UserShareCode data){
        redisTemplate.opsForSet().remove("data" ,JSONObject.toJSONString(data));
    }
    public void popById(long Id){
        UserShareCode object = findById(Id);
        if (object != null){
            pop(object);
        }
    }
}

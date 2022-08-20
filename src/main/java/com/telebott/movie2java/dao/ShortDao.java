package com.telebott.movie2java.dao;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.ShortLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
@Repository
public class ShortDao {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final Timer timer = new Timer();
    public void pushData(ShortLink data){
        ShortLink object = findById(data.getId());
        if (object != null){
            pop(object);
        }
        redisTemplate.opsForSet().add("shortLink", JSONObject.toJSONString(data));
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                popOrder(object);
//            }
//        }, 1000 * 60 * 30);
    }
    public boolean contains(ShortLink link){
        return Objects.requireNonNull(redisTemplate.opsForSet().members("shortLink")).contains(JSONObject.toJSONString(link));
    }
    public ShortLink findById(String Id){
        Set orders = redisTemplate.opsForSet().members("shortLink");
        if (orders != null){
            for (Object o: orders) {
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                if (Id.equals(jsonObject.getString("id"))){
                    return JSONObject.toJavaObject(jsonObject,ShortLink.class);
                }
            }
        }
        return null;
    }
    public void pop(ShortLink data){
        redisTemplate.opsForSet().remove("shortLink" ,JSONObject.toJSONString(data));
    }
    public void popById(String Id){
        ShortLink object = findById(Id);
        if (object != null){
            pop(object);
        }
    }
}

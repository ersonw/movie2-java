package com.telebott.movie2java.dao;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telebott.movie2java.data.EPayData;
import com.telebott.movie2java.data.InfoData;
import com.telebott.movie2java.data.SearchData;
import com.telebott.movie2java.data.SmsCode;
import com.telebott.movie2java.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;

@Slf4j
@Component
public class AuthDao {
    @Autowired
    UserDao userDao;
    @Autowired
    RedisTemplate redisTemplate;
    @Resource
    private RedisTemplate<String, User> userRedisTemplate;
    @Resource
    private RedisTemplate<String, SmsCode> codeRedisTemplate;
    @Resource
    private RedisTemplate<String, SearchData> searchRedisTemplate;
    @Resource
    private RedisTemplate<String, EPayData> orderRedisTemplate;
    private static final Timer timer = new Timer();
    public void pushInfo(int count, int type){
        InfoData info = findInfoDataType(type);
        if (info == null){
            info = new InfoData();
            info.setType(type);
            info.setAddTime(System.currentTimeMillis());
        }else {
            Set infos = redisTemplate.opsForSet().members("infos");
            if (infos != null){
                for (Object o: infos) {
                    JSONObject jsonObject = JSONObject.parseObject(o.toString());
                    if (type == jsonObject.getInteger("type")){
                        redisTemplate.opsForSet().remove("infos" ,JSONObject.toJSONString(JSONObject.toJavaObject(jsonObject,InfoData.class)));
                    }
                }
            }
        }
        info.setUpdateTime(System.currentTimeMillis());
        info.setCount(info.getCount()+count);
        redisTemplate.opsForSet().add("infos",JSONObject.toJSONString(info));
    }
    public void popInfo(int type){
        InfoData info = findInfoDataType(type);
        if (info == null){
            redisTemplate.opsForSet().remove("infos" ,JSONObject.toJSONString(info));
        }
    }
    public InfoData findInfoDataType(int type) {
        Set infos = redisTemplate.opsForSet().members("infos");
        if (infos != null){
            for (Object o: infos) {
                JSONObject jsonObject = JSONObject.parseObject(o.toString());
                if (type == jsonObject.getInteger("type")){
                    return JSONObject.toJavaObject(jsonObject,InfoData.class);
                }
            }
        }
        return null;
    }

    public void pushOrder(EPayData data){
        orderRedisTemplate.opsForValue().set(data.getOut_trade_no(),data, Duration.of(60*15,SECONDS));
    }
    public EPayData findOrderByOrderId(String orderId){
        return orderRedisTemplate.opsForValue().get(orderId);
    }
    public void popOrder(EPayData data){
        orderRedisTemplate.delete(data.getOut_trade_no());
    }
    public void popOrderById(String orderId){
        EPayData object = findOrderByOrderId(orderId);
        while (object != null){
            popOrder(object);
            object = findOrderByOrderId(orderId);
        }
    }
    private List<EPayData> getAllOrder() {
        Set<String> keys = codeRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return orderRedisTemplate.opsForValue().multiGet(keys);
    }

    public void pushSearch(SearchData data){
        searchRedisTemplate.opsForValue().set(data.getId(),data, Duration.of(60*15,SECONDS));
    }
    public List<SearchData> findSearchByUserId(long userId){
        List<SearchData> dataList = getAllSearch();
        List<SearchData> list = new ArrayList<>();
        for(SearchData data: dataList){
            if (data.getUserId() == userId){
                list.add(data);
            }
        }
        return list;
    }
    public SearchData findSearch(String id){
        List<SearchData> dataList = getAllSearch();
        for(SearchData data: dataList){
            if (data.getId().equals(id)){
                return data;
            }
        }
        return null;
    }
    public void popSearch(SearchData data){
        searchRedisTemplate.delete(data.getId());
    }
    private List<SearchData> getAllSearch() {
        Set<String> keys = codeRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return searchRedisTemplate.opsForValue().multiGet(keys);
    }

    public void pushCode(SmsCode smsCode){
        codeRedisTemplate.opsForValue().set(smsCode.getId(),smsCode, Duration.of(60*60,SECONDS));
    }
    public SmsCode findCode(String id){
        return codeRedisTemplate.opsForValue().get(id);
    }
    public void removeByPhone(String phone){
        SmsCode code = findByPhone(phone);
        while (code != null){
            codeRedisTemplate.delete(code.getId());
            code = findByPhone(phone);
        }
    }
    public SmsCode findByPhone(String phone){
        List<SmsCode> codes = getAllCodes();
        for (SmsCode code: codes) {
            if (code.getPhone().equals(phone)) return code;
        }
        return null;
    }
    private List<SmsCode> getAllCodes() {
        Set<String> keys = codeRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return codeRedisTemplate.opsForValue().multiGet(keys);
    }
    public void popCode(SmsCode code){
        codeRedisTemplate.delete(code.getId());
    }

    public void pushUser(User userToken){
        popUser(userToken);
        userRedisTemplate.opsForValue().set(userToken.getToken(),userToken);
    }
    private List<User> getAllUsers() {
        Set<String> keys = userRedisTemplate.keys("*");
        if (keys == null) return new ArrayList<>();
        return userRedisTemplate.opsForValue().multiGet(keys);
    }
    public void popUser(User userToken){
        List<User> users = getAllUsers();
        for (User user : users) {
//            System.out.println(user);
            if (user.getId() == userToken.getId()) {
                userRedisTemplate.delete(user.getToken());
            }
        }
    }

    public User findUserByToken(String token) {
        return userRedisTemplate.opsForValue().get(token);
    }
    public User findUserByUserId(long userId) {
        List<User> users = getAllUsers();
        for (User user: users) {
            if (user.getId() == userId) return user;
        }
        return null;
    }
}

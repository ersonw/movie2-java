package com.telebott.movie2java.dao;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telebott.movie2java.data.SmsCode;
import com.telebott.movie2java.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Repository
public class AuthDao {
    @Autowired
    UserDao userDao;
    @Autowired
    RedisTemplate redisTemplate;
    private static final Timer timer = new Timer();
    public void pushCode(SmsCode smsCode){
        SmsCode object = findCode(smsCode.getId());
        if (object != null){
            popCode(object);
        }
//        System.out.println(smsCode);
//        redisTemplate.opsForSet().add("smsCode",JSONObject.toJSONString(smsCode),5, TimeUnit.MINUTES);
        redisTemplate.opsForSet().add("smsCode",JSONObject.toJSONString(smsCode));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                popCode(smsCode);
            }
        }, 1000 * 60 * 60);
    }
    public SmsCode findCode(String id){
        Set smsCode = redisTemplate.opsForSet().members("smsCode");
        if (smsCode != null){
            JSONObject jsonObject = new JSONObject();
            for (Object code: smsCode) {
//                System.out.println(code);
                jsonObject = JSONObject.parseObject(code.toString());
                if (id.equals(jsonObject.get("id"))){
                    return JSONObject.toJavaObject(jsonObject,SmsCode.class);
                }
            }
        }
        return null;
    }
    public void removeByPhone(String phone){
        Set smsCode = redisTemplate.opsForSet().members("smsCode");
        if (smsCode != null){
            JSONObject jsonObject = new JSONObject();
            for (Object code: smsCode) {
                jsonObject = JSONObject.parseObject(code.toString());
                if (phone.equals(jsonObject.get("phone"))){
                    popCode(JSONObject.toJavaObject(jsonObject,SmsCode.class));
                }
            }
        }
    }
    public SmsCode findByPhone(String phone){
        Set smsCode = redisTemplate.opsForSet().members("smsCode");
        if (smsCode != null){
            JSONObject jsonObject = new JSONObject();
            for (Object code: smsCode) {
                jsonObject = JSONObject.parseObject(code.toString());
                if (phone.equals(jsonObject.get("phone"))){
                    return (JSONObject.toJavaObject(jsonObject,SmsCode.class));
                }
            }
        }
        return null;
    }
    public void popCode(SmsCode code){
        redisTemplate.opsForSet().remove("smsCode" ,JSONObject.toJSONString(code));
    }
    public void pushUser(User userToken){
        if (StringUtils.isNotEmpty(userToken.getToken())) {
            Set users = redisTemplate.opsForSet().members("Users");
            assert users != null;
//        System.out.println(users.toString());
            for (Object user: users) {
                ObjectMapper objectMapper = new ObjectMapper();
                User userEntity = objectMapper.convertValue(user, User.class);
                if (userEntity.getToken().equals(userToken.getToken()) || (userEntity.getId() > 0 && userToken.getId() > 0 && userEntity.getId() == userToken.getId())){
                    popUser(userEntity);
                }
            }
            redisTemplate.opsForSet().add("Users",userToken);
        }
    }
    public void updateUser(String token){
        User user = findUserByToken(token);
        if (user != null){
            if (user.getId() > 0){
                user = userDao.findAllById(user.getId());
                if (user != null){
                    user.setToken(token);
                    pushUser(user);
                }
            }else {
                pushUser(user);
            }
        }
    }
    public void removeUser(User userToken){
        Set users = redisTemplate.opsForSet().members("Users");
        if (users != null){
            for (Object user: users) {
                ObjectMapper objectMapper = new ObjectMapper();
                User userEntity = objectMapper.convertValue(user,User.class);
                if (userEntity.getToken().equals(userToken.getToken())){
                    popUser(userEntity);
                }
            }
        }
    }
    public void popUser(User userToken){
        redisTemplate.opsForSet().remove("Users" ,userToken);
    }
//    public User findUserByIdentifier(String id) {
//        Set users = redisTemplate.opsForSet().members("Users");
//        if (users != null){
//            for (Object user: users) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                User userEntity = objectMapper.convertValue(user,User.class);
//                if (userEntity.getIdentifier().equals(id)){
//                    return userEntity;
//                }
//            }
//        }
//        return null;
//    }
    public User findUserByToken(String token) {
        Set users = redisTemplate.opsForSet().members("Users");
        if (users != null){
            for (Object user: users) {
                ObjectMapper objectMapper = new ObjectMapper();
                User userEntity = objectMapper.convertValue(user,User.class);
                if (userEntity.getToken().equals(token)){
                    return userEntity;
                }
            }
        }
        return null;
    }
    public User findUserByUserId(long userId) {
        Set users = redisTemplate.opsForSet().members("Users");
        if (users != null){
            for (Object user: users) {
                ObjectMapper objectMapper = new ObjectMapper();
                User userEntity = objectMapper.convertValue(user,User.class);
                if (userEntity.getId()== userId){
                    return userEntity;
                }
            }
        }
        return null;
    }

    public Set getAllUser(){
        return redisTemplate.opsForSet().members("Users");
    }
}

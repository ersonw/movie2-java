package com.telebott.movie2java.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "user")
@Cacheable
@ToString(includeFieldNames = true)
public class User {
    @Id
    @GeneratedValue
    private long id;
    private String avatar;
    private String nickname;
    private String text;
    private String username;
    private String phone;
    private String email;
    private String salt;
    private String password;
    private String registerIp;
    private int status;
    private long addTime;
    private long updateTime;
    @Transient
    private String token;

    public static User getInstance(String s){
        JSONObject object = JSONObject.parseObject(s);
        return JSONObject.toJavaObject(object, User.class);
    }
    public static User getUser(String s){
        if (StringUtils.isEmpty(s)) return null;
        JSONObject object = JSONObject.parseObject(s);
        return JSONObject.toJavaObject(object, User.class);
    }
}

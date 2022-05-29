package com.telebott.movie2java.util;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.entity.User;

public class FromUtil {
    public static User fromUser(String user){
        return JSONObject.toJavaObject(JSONObject.parseObject(user), User.class);
    }
}

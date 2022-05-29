package com.telebott.movie2java.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class ConvertUtils {
    //object String 转换传入类型
    public static Object convert(Object value, Class<?> parameterType) {
        if (String.class.equals(parameterType)) {
            return value.toString();
        } else if (Integer.class.equals(parameterType)) {
            return Integer.valueOf(value.toString());
        } else if (Boolean.class.equals(parameterType)){
            return Boolean.valueOf(value.toString());
        } else if (Date.class.equals(parameterType)){
            return new Date(Long.parseLong(value.toString()));
        } else if (Long.class.equals(parameterType)){
            return Long.valueOf(value.toString());
        } else if (Double.class.equals(parameterType)){
            return Double.valueOf(value.toString());
        }else if (Float.class.equals(parameterType)){
            return Float.valueOf(value.toString());
        }else if (JSONObject.class.equals(parameterType)){
            return JSONObject.parseObject(value.toString());
        }else if (JSONArray.class.equals(parameterType)){
            return JSONArray.parseArray(value.toString());
        }else {
            return value;
        }
    }
}

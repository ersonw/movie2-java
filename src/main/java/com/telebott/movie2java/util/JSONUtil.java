package com.telebott.movie2java.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class JSONUtil {

    public static Map<String, String> toStringMap(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Object> entry: object.entrySet()) {
            if (entry.getValue() != null){
                map.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return map;
    }
    public static JSONObject getSortJson(JSONObject json) {
        if (Objects.isNull(json)) {
            return new JSONObject();
        }
        Set<String> keySet = json.keySet();
        SortedMap<String, Object> map = new TreeMap<>();
        for (String key:keySet) {
            Object value = json.get(key);
            if (Objects.nonNull(value) && value instanceof JSONArray) {
                JSONArray array = json.getJSONArray(key);
                JSONArray jsonArray = new JSONArray(new LinkedList<>());
                for (int i=0;i<array.size();i++) {
                    JSONObject sortJson = getSortJson(array.getJSONObject(i));
                    jsonArray.add(sortJson);
                }
                map.put(key, jsonArray);
            } else if (Objects.nonNull(value) && value instanceof JSONObject) {
                JSONObject sortJson = getSortJson(json.getJSONObject(key));
                map.put(key, sortJson);
            } else {
                map.put(key, value);
            }
        }
        return new JSONObject(map);
    }

}

package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.SmsConfigDao;
import com.telebott.movie2java.dao.SmsRecordDao;
import com.telebott.movie2java.entity.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsBaoService {
    @Autowired
    private SmsConfigDao configDao;

    public JSONObject getSmsConfig(){
        JSONObject object = new JSONObject();
        object.put("user",getValueByKey("smsBaoUser"));
        object.put("passwd",getValueByKey("smsBaoPassword"));
        object.put("name",getValueByKey("smsBaoName"));
        return  object;
    }
    public String getValueByKey(String name){
        List<SmsConfig> configs = configDao.findAllByName(name);
        if (configs.size() > 0) {
            return configs.get(0).getVal();
        }
        return null;
    }
}

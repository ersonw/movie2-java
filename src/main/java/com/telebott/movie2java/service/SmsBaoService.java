package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsBaoService {
//    @Autowired
//    private SystemConfigService systemConfigService;
    public JSONObject getSmsConfig(){
        JSONObject object = new JSONObject();
//        object.put("user",systemConfigService.getValueByKey("sms_bao_user"));
//        object.put("passwd",systemConfigService.getValueByKey("sms_bao_password"));
//        object.put("name",systemConfigService.getValueByKey("sms_bao_name"));
        return  object;
    }
}

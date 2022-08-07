package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MyProfileService {
    @Autowired
    private UserConfigDao userConfigDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserBalanceDiamondDao userBalanceDiamondDao;
    @Autowired
    private UserBalanceCashDao userBalanceCashDao;
    @Autowired
    private UserBalanceCoinDao userBalanceCoinDao;

    public boolean getConfigBool(String name){
        return getConfigLong(name) > 0;
    }
    public long getConfigLong(String name){
        String value = getConfig(name);
        if(value == null) return 0;
        return Long.parseLong(value);
    }
    public String getConfig(String name){
        List<UserConfig> configs = userConfigDao.findAllByName(name);
        return configs.isEmpty() ? null : configs.get(0).getVal();
    }

    public ResponseData info(User user, String ip) {
        if (user == null) return ResponseData.error("");
        User profile = userDao.findAllById(user.getId());
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        JSONObject json = new JSONObject();
        json.put("user", userService.getUserInfo(profile));
        json.put("diamond", userBalanceDiamondDao.getAllByBalance(user.getId()));
        json.put("cash", userBalanceCashDao.getAllByBalance(user.getId()));
        json.put("coin", userBalanceCoinDao.getAllByBalance(user.getId()));
        return ResponseData.success(json);
    }
}

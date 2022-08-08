package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.MobileRegularExp;
import com.telebott.movie2java.util.ToolsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private UserSpreadRecordDao userSpreadRecordDao;
    @Autowired
    private UserSpreadRebateDao userSpreadRebateDao;

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
        json.put("gain", userSpreadRebateDao.getAllByBalance(user.getId()));
        json.put("carUrl", getConfig("carUrl"));
        json.put("serviceUrl", getConfig("serviceUrl"));
        JSONObject data = new JSONObject();
        data.put("buyDiamond", getConfigBool("buyDiamond"));
        data.put("buyCoin", getConfigBool("buyCoin"));
        data.put("money", getConfigBool("money"));
        data.put("collect", getConfigBool("collect"));
        data.put("download", getConfigBool("download"));
        if(StringUtils.isNotEmpty(json.getString("carUrl"))) data.put("openCar", getConfigBool("openCar"));
        data.put("myVideo", getConfigBool("myVideo"));
        if(StringUtils.isNotEmpty(json.getString("serviceUrl"))) data.put("service", getConfigBool("service"));
        json.put("appData", data);
        return ResponseData.success(json);
    }
    public JSONObject getEdit(User user) {
        JSONObject json = new JSONObject();
        json.put("nickname", user.getNickname());
        json.put("username", user.getUsername());
        json.put("phone", user.getPhone());
        json.put("text", user.getText());
        json.put("email", user.getEmail());
        json.put("avatar", user.getAvatar());
        return json;
    }
    public ResponseData edit(User user, String ip) {
        if (user == null) return ResponseData.error("");
        User profile = userDao.findAllById(user.getId());
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        return ResponseData.success(getEdit(profile));
    }

    public ResponseData editSave(String nickname, String username, String phone, String email, String text, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if(StringUtils.isEmpty(nickname) || nickname.length() > 20 || nickname.length() < 3 || ToolsUtil.filterWords(nickname)) return ResponseData.error("昵称长度大于20或者小于3或者包含敏感字符");
        if(StringUtils.isEmpty(username) || username.length() > 20 || username.length() < 6 || ToolsUtil.checkChinese(username)) return ResponseData.error("用户名长度大于20或者小于6或者包含中文字符");
        if(text.length() > 30 || ToolsUtil.filterWords(text)) return ResponseData.error("自我介绍长度超过30或者包含敏感字符");
//        if(StringUtils.isEmpty(phone) || !MobileRegularExp.isMobileNumber(phone)) return ResponseData.error("手机号格式不正确！");
        if(!ToolsUtil.checkEmailFormat(email)) return ResponseData.error("邮箱格式不正确！");
        User profile = userDao.findAllById(user.getId());
        profile.setNickname(nickname);
        profile.setUsername(username);
        if(StringUtils.isNotEmpty(email)) profile.setEmail(email);
        if (StringUtils.isNotEmpty(text)) profile.setText(text);
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        return ResponseData.success("资料修改成功!",getEdit(profile));
    }
}

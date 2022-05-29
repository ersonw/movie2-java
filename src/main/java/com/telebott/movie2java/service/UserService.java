package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.entity.UserDeviceRecord;
import com.telebott.movie2java.entity.UserFailLoginRecord;
import com.telebott.movie2java.entity.UserLoginRecord;
import com.telebott.movie2java.util.MD5Util;
import com.telebott.movie2java.util.MobileRegularExp;
import com.telebott.movie2java.util.TimeUtil;
import com.telebott.movie2java.util.ToolsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserLoginRecordDao loginRecordDao;
    @Autowired
    private UserDeviceRecordDao deviceRecordDao;
    @Autowired
    private UserFailLoginRecordDao failLoginRecordDao;

    private static long FAIL_LOGIN_TIMES = 5;
    private long checkFailLogin(long userId){
        List<UserFailLoginRecord> records = failLoginRecordDao.checkUserToday(userId, TimeUtil.getTodayZero());
        return FAIL_LOGIN_TIMES - records.size();
    }
    public ResponseData login(String username, String password, String deviceId, String platform,String ip) {
        User user = null;
        if (MobileRegularExp.isMobileNumber(username)){
            user = userDao.findByPhone(username);
        }else {
            user = userDao.findByUsername(username);
        }
        if (user == null) {
            return ResponseData.error("账号密码不存在！");
        }
        long fail = checkFailLogin(user.getId());
        if (fail == 0) {
            return ResponseData.error("今日密码重试超过上限,请明天重试！");
        }
        MD5Util md5Util = new MD5Util(user.getSalt());
        String passwd = md5Util.getPassWord(password);
        if (passwd.equals(user.getPassword())){
            user.setToken(ToolsUtil.getToken());
            UserLoginRecord loginRecord = new UserLoginRecord();
            loginRecord.setUserId(user.getId());
            loginRecord.setDeviceId(deviceId);
            loginRecord.setPlatform(platform);
            loginRecord.setAddTime(System.currentTimeMillis());
            loginRecord.setIp(ip);
            loginRecordDao.saveAndFlush(loginRecord);
            UserDeviceRecord deviceRecord = null;
            if (StringUtils.isNotEmpty(deviceId)){
                deviceRecord = deviceRecordDao.findAllByUserIdAndDeviceId(user.getId(),deviceId);
            }else {
                deviceRecord = deviceRecordDao.findAllByUserId(user.getId());
            }
            if (deviceRecord == null){
                deviceRecord = new UserDeviceRecord();
                deviceRecord.setDeviceId(deviceId);
                deviceRecord.setPlatform(platform);
                deviceRecord.setUserId(user.getId());
            }
            deviceRecord.setAddTime(System.currentTimeMillis());
            deviceRecordDao.saveAndFlush(deviceRecord);
            authDao.pushUser(user);
            return ResponseData.success((JSONObject) (new JSONObject()).put("token", user.getToken()));
        }else {
            fail--;
            UserFailLoginRecord record = new UserFailLoginRecord();
            record.setUserId(user.getId());
            record.setDeviceId(deviceId);
            record.setPlatform(platform);
            record.setAddTime(System.currentTimeMillis());
            failLoginRecordDao.saveAndFlush(record);
            return ResponseData.error("密码错误！剩余"+fail+"次尝试机会");
        }
    }
    //改为只能手机注册 增加验证码逻辑 增加发送验证码
    public ResponseData register(String username, String password,String codeId,String code, String deviceId, String platform,String ip) {
        User user = null;
        if (MobileRegularExp.isMobileNumber(username)){
            user = userDao.findByPhone(username);
        }else {
            user = userDao.findByUsername(username);
        }
        if (user == null) {
            return ResponseData.error("账号已存在！");
        }
        user = new User();
        user.setRegisterIp(ip);
        if (MobileRegularExp.isMobileNumber(username)){
            user.setPhone(username);
            user.setNickname(username.substring(0,4)+username.substring(8));
        }else {
            user.setUsername(username);
            user.setNickname(username);
        }
        user.setSalt(ToolsUtil.getSalt());
        MD5Util md5Util = new MD5Util(user.getSalt());
        user.setPassword(md5Util.getPassWord(password));
        user.setStatus(1);
        user.setAddTime(System.currentTimeMillis());
        user.setUpdateTime(user.getAddTime());
        user.setText("本人很懒，不想说话！");
        userDao.saveAndFlush(user);
        return login(username,password,deviceId,platform,ip);
    }
}

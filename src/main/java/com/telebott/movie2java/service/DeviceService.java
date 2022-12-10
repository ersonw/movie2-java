package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.UserDao;
import com.telebott.movie2java.dao.UserDeviceRecordDao;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.entity.UserDeviceRecord;
import com.telebott.movie2java.util.SmsBaoUtil;
import com.telebott.movie2java.util.ToolsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private UserDeviceRecordDao deviceRecordDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;

    public boolean check(String deviceId) {
        long records = deviceRecordDao.countAllByDeviceId(deviceId);
        return records > 0;
    }

    public User getToken(String deviceId, String ip) {
        List<UserDeviceRecord> records = deviceRecordDao.findAllByDeviceId(deviceId);
//        if (records == null || records.size() == 0) {
//            return null;
//        }
        for (UserDeviceRecord record : records) {
            User user = authDao.findUserByUserId(record.getUserId());
            if (user != null) {
                record.setIp(ip);
                record.setAddTime(System.currentTimeMillis());
                deviceRecordDao.saveAndFlush(record);
                user.setToken(ToolsUtil.getToken());
                authDao.pushUser(user);
                return user;
            }
        }
        return null;
//        return authDao.findUserByUserId(records.get(0).getUserId());
    }

    public ResponseData checkDevice(String deviceId, String ip) {
        User user = getToken(deviceId, ip);
//        if(!check(deviceId)){
        if(user == null){
            user = new User();
            user.setNickname("春潮视频_游客"+ SmsBaoUtil.getSmsCode());
            user.setText("春潮视频萌新，待机中哟！");
            user.setUsername(ToolsUtil.getRandom(6));
            user.setStatus(1);
            user.setAddTime(System.currentTimeMillis());
            user.setUpdateTime(System.currentTimeMillis());
            user.setRegisterIp(ip);
            user.setPhone("");
            userDao.saveAndFlush(user);
            user.setToken(ToolsUtil.getToken());
            authDao.pushUser(user);
//            return ResponseData.fail();
        }
//        String token = getToken(deviceId);
//        if (token == null) {}
//        if (user == null) {
//            return ResponseData.fail();
//        }
        return ResponseData.success(userService.getUserInfo(user));
    }
}

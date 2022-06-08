package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.UserDeviceRecordDao;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.entity.UserDeviceRecord;
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
                return user;
            }
        }
        return null;
//        return authDao.findUserByUserId(records.get(0).getUserId());
    }

    public ResponseData checkDevice(String deviceId, String ip) {
        if(!check(deviceId)){
            return ResponseData.fail();
        }
//        String token = getToken(deviceId);
//        if (token == null) {}
        User user = getToken(deviceId, ip);
        if (user == null) {
            return ResponseData.fail();
        }
        return ResponseData.success(userService.getUserInfo(user));
    }
}

package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.AuthDao;
import com.telebott.movie2java.dao.UserDeviceRecordDao;
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

    public boolean check(String deviceId) {
        long records = deviceRecordDao.countAllByDeviceId(deviceId);
        return records > 0;
    }

    public String getToken(String deviceId) {
        List<UserDeviceRecord> records = deviceRecordDao.findAllByDeviceId(deviceId);
        if (records == null || records.size() == 0) {
            return null;
        }
        User user = authDao.findUserByUserId(records.get(0).getUserId());
        if (user == null) {
            return null;
        }
        return user.getToken();
    }
}

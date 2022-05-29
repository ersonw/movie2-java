package com.telebott.movie2java.service;

import com.telebott.movie2java.dao.UserDao;
import com.telebott.movie2java.dao.UserDeviceRecordDao;
import com.telebott.movie2java.dao.UserLoginRecordDao;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.entity.User;
import com.telebott.movie2java.entity.UserDeviceRecord;
import com.telebott.movie2java.util.MD5Util;
import com.telebott.movie2java.util.MobileRegularExp;
import com.telebott.movie2java.util.ToolsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserLoginRecordDao loginRecordDao;
    @Autowired
    private UserDeviceRecordDao deviceRecordDao;
    public ResponseData login(String username, String password, String deviceId, String platform) {
        User user = null;
        if (MobileRegularExp.isMobileNumber(username)){
            user = userDao.findByPhone(username);
        }else {
            user = userDao.findByUsername(username);
        }
        if (user == null) {
            return ResponseData.error("账号密码不存在！");
        }
        MD5Util md5Util = new MD5Util(user.getSalt());
        String passwd = md5Util.getPassWord(password);
        if (passwd.equals(user.getPassword())){
            user.setToken(ToolsUtil.getToken());
            UserDeviceRecord deviceRecord = new UserDeviceRecord();
        }else {
            return ResponseData.error("密码错误！");
        }
    }

}

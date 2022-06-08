package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.SmsCode;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.*;
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
    private MembershipExperienceDao membershipExperienceDao;
    @Autowired
    private SmsRecordDao smsRecordDao;
    @Autowired
    private SmsBaoService smsBaoService;
    @Autowired
    private UserLoginRecordDao loginRecordDao;
    @Autowired
    private UserDeviceRecordDao deviceRecordDao;
    @Autowired
    private UserFailLoginRecordDao failLoginRecordDao;

    private static long FAIL_LOGIN_TIMES = 6;
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
//        User user = userDao.findByPhone();
        if (user == null) {
            return ResponseData.error("账号密码不存在！");
        }
        long fail = checkFailLogin(user.getId());
        if (fail == 0) {
            return ResponseData.error("今日密码重试次数已达上限，账号已锁定,请明天重试！");
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
            deviceRecord.setIp(ip);
            deviceRecord.setAddTime(System.currentTimeMillis());
            deviceRecordDao.saveAndFlush(deviceRecord);
            authDao.pushUser(user);
            return ResponseData.success(getUserInfo(user));
        }else {
            fail--;
            UserFailLoginRecord record = new UserFailLoginRecord();
            record.setUserId(user.getId());
            record.setDeviceId(deviceId);
            record.setPlatform(platform);
            record.setIp(ip);
            record.setAddTime(System.currentTimeMillis());
            failLoginRecordDao.saveAndFlush(record);
            return ResponseData.error("密码错误！剩余"+fail+"次尝试机会");
        }
    }
    public JSONObject getUserInfo(User user){
        JSONObject object = ResponseData.object("token", user.getToken());
        object.put("id",user.getId());
        object.put("avatar",user.getAvatar());
        object.put("nickname",user.getNickname());
        object.put("text",user.getText());
        object.put("username",user.getUsername());
        object.put("phone",user.getPhone());
        object.put("email",user.getEmail());
        object.put("level", membershipExperienceDao.countByUserId(user.getId()));
        return object;
    }
    //改为只能手机注册 增加验证码逻辑 增加发送验证码
    public ResponseData register(String password,String codeId,String code,String ip) {
        String phone = verifyCode(codeId,code);
        if (phone == null){
            return ResponseData.error("验证码不正确！");
        }
        User user = userDao.findByPhone(phone);
        if (user != null) {
            return ResponseData.error("手机号已注册！");
        }
        user = new User();
        user.setRegisterIp(ip);
        user.setPhone(phone);
        user.setNickname(phone.substring(0,4)+"****"+phone.substring(8));
        user.setSalt(ToolsUtil.getSalt());
        MD5Util md5Util = new MD5Util(user.getSalt());
        user.setPassword(md5Util.getPassWord(password));
        user.setStatus(1);
        user.setAddTime(System.currentTimeMillis());
        user.setUpdateTime(user.getAddTime());
        user.setText("本人很懒，不想说话！");
        userDao.saveAndFlush(user);
//        return login(username,password,deviceId,platform,ip);
        return ResponseData.success(ResponseData.object("id", user.getId()));
    }
    public ResponseData sendSmsRegister(String phone, String ip){
        if (!MobileRegularExp.isMobileNumber(phone)){
            return ResponseData.fail("手机号码格式错误！");
        }
        User user = userDao.findByPhone(phone);
        if (user != null){
            return ResponseData.fail("手机号已注册，可以直接跳转登陆页面哟！");
        }
        if (!checkSmsMax(phone)){
            return ResponseData.fail("今日短信发送已达上限！");
        }
        long last = checkSmsLast(phone);
        if (last > 0){
            return ResponseData.fail("操作过于频繁，请在"+last+"秒后重试！");
        }
        SmsCode code= _getCode(phone);
        if (code == null){
            code = new SmsCode(phone);
            authDao.removeByPhone(phone);
            authDao.pushCode(code);
        }
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setIp(ip);
        smsRecord.setCode(code.getCode());
        smsRecord.setPhone(code.getPhone());
        smsRecord.setStatus(0);
        smsRecord.setAddTime(System.currentTimeMillis());
        smsRecordDao.saveAndFlush(smsRecord);
        if (SmsBaoUtil.sendSmsCode(code)){
            smsRecord.setStatus(1);
            smsRecordDao.saveAndFlush(smsRecord);
            return ResponseData.success(ResponseData.object("id", code.getId()));
        }
        authDao.popCode(code);
        return ResponseData.fail("短信发送失败，请联系管理员!");
    }
    public SmsCode _getCode(String phone){
        SmsCode smsCode = authDao.findByPhone(phone);
        if (smsCode != null){
            return smsCode;
        }
        return null;
    }
    private long checkSmsLast(String phone){
        SmsRecord record = smsRecordDao.getLast(phone);
        if (record == null){
            return 0;
        }
        long last = System.currentTimeMillis() - record.getAddTime();
        long ms = 1000 * 60 * 2;
        if (last > ms){
            return 0;
        }
        return (ms - last) / 1000;
    }
    private boolean checkSmsMax(String phone){
        long count = smsRecordDao.countTodayMax(TimeUtil.getTodayZero(),phone);
        long max = Long.parseLong(smsBaoService.getValueByKey("smsCountMaxDay"));
        return count < max;
    }
    public String verifyCode(String id, String code){
        SmsCode smsCode = authDao.findCode(id);
        if (smsCode != null && smsCode.getCode().equals(code)){
            authDao.popCode(smsCode);
//            SmsRecords smsRecords = smsRecordsDao.findAllByData(smsCode.getId());
//            smsRecords.setStatus(2);
//            smsRecordsDao.saveAndFlush(smsRecords);
            return smsCode.getPhone();
        }
        return null;
    }
}

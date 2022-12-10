package com.telebott.movie2java.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.dao.*;
import com.telebott.movie2java.data.ResponseData;
import com.telebott.movie2java.data.SmsCode;
import com.telebott.movie2java.entity.*;
import com.telebott.movie2java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private MembershipExperienceDao membershipExperienceDao;
    @Autowired
    private MembershipLevelDao membershipLevelDao;
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
    @Autowired
    private MembershipExpiredDao membershipExpiredDao;
    @Autowired
    private ShortVideoDao shortVideoDao;
    @Autowired
    private UserBalanceCashDao userBalanceCashDao;
    @Autowired
    private UserBalanceDiamondDao userBalanceDiamondDao;
    @Autowired
    private UserFollowDao userFollowDao;
    @Autowired
    private ShortVideoLikeDao shortVideoLikeDao;
    @Autowired
    private ShortVideoService shortVideoService;

    @Autowired
    private MembershipService membershipService;
    @Autowired
    private GameService gameService;
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private UserShareRecordDao userShareRecordDao;
    @Autowired
    private UserShareConfigDao userShareConfigDao;
    @Autowired
    private UserSpreadRecordDao userSpreadRecordDao;

    private static long FAIL_LOGIN_TIMES = 6;
    private static String SHARE_CONFIG_V1 = "v1";
    private static String SHARE_CONFIG_V1S = "v1s";
    private static String SHARE_CONFIG_V1V = "v1v";
    private static String SHARE_CONFIG_V2 = "v2";
    private static String SHARE_CONFIG_V2S = "v2s";
    private static String SHARE_CONFIG_V2V = "v2v";
    private static String SHARE_CONFIG_V3 = "v3";
    private static String SHARE_CONFIG_V3S = "v3s";
    private static String SHARE_CONFIG_V3V = "v3v";

    public boolean isMembership(long userId) {
        return getMember(userId);
    }
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
        object.put("expired",getExpired(user.getId()));
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            object.put("phone", phone.substring(0, 4) + "****" + phone.substring(phone.length() - 4));
        }
        object.put("email",user.getEmail());
        object.put("member",getMember(user.getId()));
        object.put("level", getMemberLevel(user.getId()));
        return object;
    }
    public long getExpired(long userId){
        MembershipExpired expired = membershipExpiredDao.findAllByUserId(userId);
        if(expired!=null){
            return expired.getExpired()+expired.getAddTime();
        }
        return 0;
    }
    public boolean getMember(long userId){
        return getExpired(userId) > System.currentTimeMillis();
    }
    public long getMemberLevel(long userId){
        long level =0;
        long experience = membershipExperienceDao.getAllByUserId(userId);
        long experienced = 0;
        while (experience > experienced){
            level++;
            MembershipLevel l = membershipLevelDao.findByLevel(level);
            if (l==null){
                break;
            }
            experienced = l.getExperience();
            experience -= l.getExperience();
        }
        if(experience < 0){
            level--;
        }
        return level;
    }
    public long getExperience(long userId){
        long level =0;
        long experience = membershipExperienceDao.getAllByUserId(userId);
        long experienced = 0;
        while (experience > experienced){
//            log.info("experienced: {} experience:{}",experienced,experience);
            level++;
            MembershipLevel l = membershipLevelDao.findByLevel(level);
            if (l==null){
                break;
            }
            experienced = l.getExperience();
            experience -= l.getExperience();
        }
        if(experience < 0){
            experience = experienced+experience;
        }
        return experience;
    }
    public long getExperienced(long userId){
        MembershipLevel grade = membershipLevelDao.findByLevel(getMemberLevel(userId)+1);
        return grade.getExperience();
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
        user.setNickname("春潮用户_"+phone.substring(phone.length() - 6));
        user.setSalt(ToolsUtil.getSalt());
        MD5Util md5Util = new MD5Util(user.getSalt());
        user.setPassword(md5Util.getPassWord(password));
        user.setStatus(1);
        user.setAddTime(System.currentTimeMillis());
        user.setUpdateTime(user.getAddTime());
        user.setText("春潮视频萌新，待机中哟！");
        user.setUsername(ToolsUtil.getRandom(6));
        while (userDao.findByUsername(user.getUsername()) != null) {
            user.setUsername(ToolsUtil.getRandom(6));
        }
        userDao.saveAndFlush(user);
//        return login(username,password,deviceId,platform,ip);
        JSONObject object = ResponseData.object("id", user.getId());
        user.setToken(ToolsUtil.getToken());
        authDao.pushUser(user);
        object.put("token",user.getToken());
        object.put("nickname",user.getNickname());
        return ResponseData.success(object);
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
    public long checkSmsLast(String phone){
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
    public boolean checkSmsMax(String phone){
        long count = smsRecordDao.countTodayMax(TimeUtil.getTodayZero(),phone);
        long max = Long.parseLong(smsBaoService.getValueByKey("smsCountMaxDay"));
        return count < max;
    }
    public String verifyCode(String id, String code){
        return verifyCode(id, code, true);
    }
    public String verifyCode(String id, String code, boolean del){
        SmsCode smsCode = authDao.findCode(id);
        if (smsCode != null && smsCode.getCode().equals(code)){
            if (del)authDao.popCode(smsCode);
//            SmsRecords smsRecords = smsRecordsDao.findAllByData(smsCode.getId());
//            smsRecords.setStatus(2);
//            smsRecordsDao.saveAndFlush(smsRecords);
            return smsCode.getPhone();
        }
        return null;
    }

    public ResponseData myProfileVideo(int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12);
        Page<ShortVideo> videoPage = shortVideoDao.getAllMyVideos(user.getId(),pageable);
        JSONArray array = new JSONArray();
        for (ShortVideo video : videoPage.getContent()){
            JSONObject object = shortVideoService.getShortVideo(video,user.getId());
            if (object != null) array.add(object);
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", videoPage.getTotalPages());
        return ResponseData.success(json);
    }
    public long getProgressProfile(User user){
        long progress = 0;
        if(StringUtils.isNotEmpty(user.getNickname())) progress+=10;
        if(StringUtils.isNotEmpty(user.getAvatar())) progress+=10;
        if(StringUtils.isNotEmpty(user.getText())) progress+=10;
        if(StringUtils.isNotEmpty(user.getUsername())) progress+=10;
        if(StringUtils.isNotEmpty(user.getPhone())) progress+=10;
        if(StringUtils.isNotEmpty(user.getEmail())) progress+=10;
        return progress;
    }
    public ResponseData myProfile(User user, String ip) {
        if (user == null) return ResponseData.error("");
        User profile = userDao.findAllById(user.getId());
        profile.setToken(user.getToken());
        authDao.pushUser(profile);
        JSONObject json = new JSONObject();
        json.put("user", getUserInfo(profile));
        json.put("progress", getProgressProfile(profile));
        json.put("addFriends", userFollowDao.countAllByToUserIdAndState(user.getId(), 0));
//        json.put("works", getProgressProfile(profile));
//        json.put("cash", userBalanceCashDao.getAllByBalance(user.getId()));
//        json.put("diamond", userBalanceDiamondDao.getAllByBalance(user.getId()));
        json.put("likes", shortVideoLikeDao.getAllByUserId(profile.getId()));
        json.put("follows", userFollowDao.countAllByUserId(user.getId()));
        json.put("fans", userFollowDao.countAllByToUserId(user.getId()));
        return ResponseData.success(json);
    }

    public ResponseData profileVideo(long id, int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12);
        Page<ShortVideo> videoPage = shortVideoDao.getAllProfileVideos(profile.getId(), pageable);
        JSONArray array = new JSONArray();
        for (ShortVideo video : videoPage.getContent()){
            JSONObject object = shortVideoService.getShortVideo(video,user.getId());
            if (object != null) array.add(object);
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", videoPage.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData profile(long id, User user, String ip) {
        if (id < 1) return ResponseData.error("");
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        JSONObject json = new JSONObject();
        json.put("user", getUserInfo(profile));
        json.put("likes", shortVideoLikeDao.getAllByUserId(profile.getId()));
//        json.put("works", shortVideoDao.countAllByUserIdAndStatus(profile.getId(),1));
//        json.put("cash", userBalanceCashDao.getAllByBalance(profile.getId()));
//        json.put("diamond", userBalanceDiamondDao.getAllByBalance(profile.getId()));
//        json.put("likes", shortVideoLikeDao.countAllByUserId(profile.getId()));
        json.put("follows", userFollowDao.countAllByUserId(profile.getId()));
        json.put("follow", userFollowDao.findAllByUserIdAndToUserId(user==null?0: user.getId(), profile.getId()) != null);
        json.put("fans", userFollowDao.countAllByToUserId(profile.getId()));
        return ResponseData.success(json);
    }

    public ResponseData profileVideoLike(long id, int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12);
        Page<ShortVideo> videoPage = shortVideoDao.getAllLikeProfileVideos(profile.getId(), pageable);
        JSONArray array = new JSONArray();
        for (ShortVideo video : videoPage.getContent()){
            JSONObject object = shortVideoService.getShortVideo(video,user.getId());
            if (object != null) array.add(object);
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", videoPage.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData myProfileVideoLike(int page, User user, String ip) {
        if (user == null) return ResponseData.error("未登录！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,12);
        Page<ShortVideo> videoPage = shortVideoDao.getAllLikeProfileVideos(user.getId(), pageable);
        JSONArray array = new JSONArray();
        for (ShortVideo video : videoPage.getContent()){
            JSONObject object = shortVideoService.getShortVideo(video,user.getId());
            if (object != null) array.add(object);
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", videoPage.getTotalPages());
        return ResponseData.success(json);
    }
    public JSONObject getUserFans(UserFollow userFollow, long userId){
        JSONObject object = new JSONObject();
        User user = userDao.findAllById(userFollow.getUserId());
        if (user == null) return object;
        object.put("id", user.getId());
        object.put("avatar", user.getAvatar());
        object.put("nickname", user.getNickname());
        object.put("fans", userFollowDao.countAllByToUserId(user.getId()));
        object.put("followed", userFollowDao.findAllByUserIdAndToUserId(user.getId(),userId) != null);
        object.put("member",getMember(user.getId()));
        object.put("level", getMemberLevel(user.getId()));
        if(user.getId() == userId){
            object.put("follow", true);
        }else {
            object.put("follow", userFollowDao.findAllByUserIdAndToUserId(userId, user.getId()) != null);
        }
        return object;
    }
    public JSONObject getUserFollow(UserFollow userFollow, long userId){
        JSONObject object = new JSONObject();
        User user = userDao.findAllById(userFollow.getToUserId());
        if (user == null) return object;
        object.put("id", user.getId());
        object.put("avatar", user.getAvatar());
        object.put("nickname", user.getNickname());
        object.put("fans", userFollowDao.countAllByToUserId(user.getId()));
        object.put("followed", userFollowDao.findAllByUserIdAndToUserId(user.getId(),userId) != null);
        object.put("member",getMember(user.getId()));
        object.put("level", getMemberLevel(user.getId()));
        if(user.getId() == userId){
            object.put("follow", true);
        }else {
            object.put("follow", userFollowDao.findAllByUserIdAndToUserId(userId, user.getId()) != null);
        }
        return object;
    }
    public ResponseData follows(long id, int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,24, Sort.by(Sort.Direction.DESC,"id"));
        Page<UserFollow> follows = userFollowDao.findAllByUserId(profile.getId(), pageable);
        JSONArray array = new JSONArray();
        for (UserFollow follow : follows) {
            array.add(getUserFollow(follow,user.getId()));
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", follows.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData fans(long id, int page, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,24, Sort.by(Sort.Direction.DESC,"id"));
        Page<UserFollow> fans = userFollowDao.findAllByToUserId(profile.getId(), pageable);
        JSONArray array = new JSONArray();
        for (UserFollow follow : fans) {
            array.add(getUserFans(follow,user.getId()));
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", fans.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData followsSearch(long id, int page, String text, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        if (StringUtils.isEmpty(text)) return ResponseData.error();
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,24, Sort.by(Sort.Direction.DESC,"id"));
        Page<UserFollow> follows = userFollowDao.getAllByUserId(profile.getId(),"%"+text+"%",pageable);
        JSONArray array = new JSONArray();
        for (UserFollow follow : follows) {
            array.add(getUserFollow(follow,user.getId()));
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", follows.getTotalPages());
        System.out.printf("%s\n", json);
        return ResponseData.success(json);
    }

    public ResponseData fansSearch(long id, int page, String text, User user, String ip) {
        if (user == null) return ResponseData.error("");
        if (id < 1) return ResponseData.error("用户不存在！");
        if (StringUtils.isEmpty(text)) return ResponseData.error();
        User profile = userDao.findAllById(id);
        if (profile == null) return ResponseData.error("用户不存在！");
        page--;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page,24, Sort.by(Sort.Direction.DESC,"id"));
        Page<UserFollow> fans = userFollowDao.getAllByToUserId(profile.getId(),"%"+text+"%",pageable);
        JSONArray array = new JSONArray();
        for (UserFollow follow : fans) {
            array.add(getUserFans(follow,user.getId()));
        }
        JSONObject json = ResponseData.object("list", array);
        json.put("total", fans.getTotalPages());
        return ResponseData.success(json);
    }

    public ResponseData userLoginSms(String phone, String ip) {
        if (!MobileRegularExp.isMobileNumber(phone)){
            return ResponseData.fail("手机号码格式错误！");
        }
        User user = userDao.findByPhone(phone);
        if (user == null){
            return ResponseData.fail("手机号未注册，请先注册哟！");
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

    public ResponseData userLoginPhone(String codeId, String code, String deviceId, String platform,String ip) {
        String phone = verifyCode(codeId,code);
        if (phone == null){
            return ResponseData.error("验证码不正确！");
        }
        User user = userDao.findByPhone(phone);
        if (user == null) {
            return ResponseData.error("手机号未注册！");
        }
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
    }

    public ResponseData logout(User user, String ip) {
        if (user == null) return ResponseData.error("");
//        if (StringUtils.isEmpty(user.getPhone())) return ResponseData.error("为了安全起见，游客暂不支持注销账号哟！注销账号将无法找回！");
        authDao.popUser(user);
        return ResponseData.success("未绑定手机号将无法找回原来账号！注销成功",ResponseData.object("state", true));
    }
    public String getShareConfig(String name){
        List<UserShareConfig> configList = userShareConfigDao.findAllByName(name);
        if (configList.isEmpty()) return null;
        return configList.get(0).getVal();
    }
    public long getShareConfigLong(String name){
        String val = getShareConfig(name);
        if (StringUtils.isEmpty(val)) return 0;
        if (!ToolsUtil.isNumberString(val)) return 0;
        return Long.parseLong(val);
    }
    public boolean getShareConfigBool(String name){
        if (getShareConfigLong(name) > 0) return true;
        return false;
    }
    public String getShareConfigStr(String type){
        String val =  getShareConfig(type+"s");
        if (StringUtils.isNotEmpty(val)) return  val;
        val =  getShareConfig(type+"v");
        if (StringUtils.isEmpty(val)) return  null;
//        System.out.println(val);
        String[] values = val.split(";");
        StringBuilder sb = new StringBuilder();
        for (String v: values) {
            String[] vs = v.split("=");
            switch (vs[0]) {
                case "m":
                    sb.append("会员体验").append(vs[1]).append("天").append("+");
                    break;
                case "g":
                    sb.append("游戏金币").append(vs[1]).append("个").append("+");
                    break;
                case "d":
                    sb.append("钻石").append(vs[1]).append("个").append("+");
                    break;
                default:
                    break;
            }
        }
//        System.out.println(sb);
        if (sb.toString().endsWith("+")) return sb.substring(0,sb.toString().length()-1);
        return sb.toString();
    }
    public ResponseData shareConfig(User user, String ip) {
        JSONObject object = new JSONObject();
        object.put(SHARE_CONFIG_V1, getShareConfigLong(SHARE_CONFIG_V1));
        object.put(SHARE_CONFIG_V1S, getShareConfigStr(SHARE_CONFIG_V1));

        object.put(SHARE_CONFIG_V2, getShareConfigLong(SHARE_CONFIG_V2));
        object.put(SHARE_CONFIG_V2S, getShareConfigStr(SHARE_CONFIG_V2));

        object.put(SHARE_CONFIG_V3, getShareConfigLong(SHARE_CONFIG_V3));
        object.put(SHARE_CONFIG_V3S, getShareConfigStr(SHARE_CONFIG_V3));
        String shareUrl = getShareConfig("shareUrl");
        if (shareUrl != null){
            if (!shareUrl.startsWith("http")){
                shareUrl = "http://" + shareUrl;
            }
            if (!shareUrl.endsWith("/")){
                shareUrl += "/";
            }
        }
        if (user != null){
            object.put("shareUrl", shareUrl+"?code="+user.getUsername());
            object.put("count", userSpreadRecordDao.findAllByCount(user.getId()));
            object.put("v1f", (userShareRecordDao.findAllByTypeAndUserId(SHARE_CONFIG_V1, user.getId())).size() > 0);
            object.put("v2f", (userShareRecordDao.findAllByTypeAndUserId(SHARE_CONFIG_V2, user.getId())).size() > 0);
            object.put("v3f", (userShareRecordDao.findAllByTypeAndUserId(SHARE_CONFIG_V3, user.getId())).size() > 0);
        }
        return ResponseData.success(object);
    }
    public void handlerReceive(String type, long userId,String ip){
        String val =  getShareConfig(type+"v");
        if (StringUtils.isEmpty(val)) return ;
        String[] values = val.split(";");
        for (String v: values) {
            String[] vs = v.split("=");
            switch (vs[0]) {
                case "m":
                    membershipService.handlerRegister(userId, new Long(vs[1]),"推广任务领取");
                    break;
                case "g":
                    gameService.handlerRegister(userId, new Long(vs[1]),"推广任务领取");
                    break;
                case "d":
                    diamondService.handlerRegister(userId, new Long(vs[1]),"推广任务领取");
                    break;
                default:
                    break;
            }
        }
        userShareRecordDao.saveAndFlush(new UserShareRecord(type, ip, userId));
    }
    public ResponseData shareReceive(String type, User user, String ip) {
        if(user == null) return ResponseData.error("");
        if((userShareRecordDao.findAllByTypeAndUserId(type, user.getId())).size() > 0) return ResponseData.success("已领取过该奖励!");
        handlerReceive(type,user.getId(), ip);
        return ResponseData.success("领取成功!",ResponseData.object("state", true));
    }
}
